package org.redquark.concurrency.problems.moneytransfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redquark.concurrency.problems.moneytransfer.domains.Account;
import org.redquark.concurrency.problems.moneytransfer.logger.TransferLogger;
import org.redquark.concurrency.problems.moneytransfer.repositories.AccountRepository;
import org.redquark.concurrency.problems.moneytransfer.utils.LockManager;
import org.redquark.concurrency.problems.moneytransfer.utils.RateLimiterManager;
import org.redquark.concurrency.problems.moneytransfer.validators.PositiveAmountValidator;
import org.redquark.concurrency.problems.moneytransfer.validators.SameAccountValidator;
import org.redquark.concurrency.problems.moneytransfer.validators.SufficientFundsValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MoneyTransferServiceMockitoTest {

    private AccountRepository repository;
    private MoneyTransferService moneyTransferService;

    private Account accountA1;
    private Account accountA2;

    @BeforeEach
    public void setUp() {
        repository = mock(AccountRepository.class);
        LockManager lockManager = mock(LockManager.class);
        RateLimiterManager rateLimiterManager = mock(RateLimiterManager.class);
        TransferLogger transferLogger = mock(TransferLogger.class);

        moneyTransferService = new MoneyTransferService(
                repository,
                lockManager,
                List.of(new PositiveAmountValidator(), new SameAccountValidator(), new SufficientFundsValidator()),
                rateLimiterManager,
                transferLogger
        );

        accountA1 = new Account("A1", BigDecimal.valueOf(1000.00));
        accountA2 = new Account("A2", BigDecimal.valueOf(500.00));

        when(repository.findById("A1")).thenReturn(accountA1);
        when(repository.findById("A2")).thenReturn(accountA2);
    }

    @Test
    void shouldTransferMoneyWhenSufficientBalanceExists() {
        moneyTransferService.transfer("A1", "A2", BigDecimal.valueOf(200.00));

        assertEquals(BigDecimal.valueOf(800.00), accountA1.getBalance());
        assertEquals(BigDecimal.valueOf(700.00), accountA2.getBalance());
    }

    @Test
    void shouldFailTransferWhenSourceAccountDoesNotExist() {
        when(repository.findById("X")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> moneyTransferService.transfer("X", "A2", BigDecimal.valueOf(100.00)));
    }

    @Test
    void shouldFailTransferWhenDestinationAccountDoesNotExist() {
        when(repository.findById("Y")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> moneyTransferService.transfer("A1", "Y", BigDecimal.valueOf(100.00)));
    }

    @Test
    void shouldFailTransferWhenInsufficientBalance() {
        assertThrows(IllegalArgumentException.class,
                () -> moneyTransferService.transfer("A1", "A2", BigDecimal.valueOf(1100.00)));
    }

    @Test
    void shouldFailTransferWhenTransferAmountIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> moneyTransferService.transfer("A1", "A2", BigDecimal.valueOf(-100.00)));
    }

    @Test
    void shouldFailTransferWhenSourceAndDestinationAreSame() {
        assertThrows(IllegalArgumentException.class,
                () -> moneyTransferService.transfer("A1", "A1", BigDecimal.valueOf(100.00)));
    }

    @Test
    void shouldRejectTransferWithZeroAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> moneyTransferService.transfer("A1", "A2", BigDecimal.ZERO));
    }

    @Test
    void shouldTransferMoneyCorrectlyWhenMultipleConcurrentTransactionsOccur() throws InterruptedException {
        final int numberOfThreads = 10;
        try (ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads)) {
            for (int i = 0; i < numberOfThreads; i++) {
                executorService.submit(() -> moneyTransferService.transfer("A1", "A2", BigDecimal.valueOf(10.00)));
            }
            executorService.shutdown();
            boolean isTerminated = executorService.awaitTermination(5, TimeUnit.SECONDS);
            assertTrue(isTerminated);
        }

        assertEquals(BigDecimal.valueOf(1000.00 - 10.00 * numberOfThreads), accountA1.getBalance());
        assertEquals(BigDecimal.valueOf(500.00 + 10.00 * numberOfThreads), accountA2.getBalance());
    }

    @Test
    void shouldRejectTransfersWithInvalidAccountIds() {
        when(repository.findById("Invalid")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> moneyTransferService.transfer("Invalid", "A2", BigDecimal.valueOf(100.00)));
        assertThrows(IllegalArgumentException.class,
                () -> moneyTransferService.transfer("A1", "Invalid", BigDecimal.valueOf(100.00)));
    }
}
