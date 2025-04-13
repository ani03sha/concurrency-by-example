package org.redquark.concurrency.problems.moneytransfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redquark.concurrency.problems.moneytransfer.domains.Account;
import org.redquark.concurrency.problems.moneytransfer.logger.TransferLogger;
import org.redquark.concurrency.problems.moneytransfer.repositories.InMemoryAccountRepository;
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

import static org.junit.jupiter.api.Assertions.*;

class MoneyTransferServiceTest {

    private InMemoryAccountRepository repository;
    private MoneyTransferService moneyTransferService;

    @BeforeEach
    public void setUp() {
        repository = new InMemoryAccountRepository();
        repository.addAccount(new Account("A1", BigDecimal.valueOf(1000.00)));
        repository.addAccount(new Account("A2", BigDecimal.valueOf(500.00)));

        moneyTransferService = new MoneyTransferService(
                repository,
                new LockManager(),
                List.of(new PositiveAmountValidator(), new SameAccountValidator(), new SufficientFundsValidator()),
                new RateLimiterManager(),
                new TransferLogger()
        );
    }

    @Test
    void shouldTransferMoneyWhenSufficientBalanceExists() {
        moneyTransferService.transfer("A1", "A2", BigDecimal.valueOf(200.00));
        assertEquals(BigDecimal.valueOf(800.00), repository.findById("A1").getBalance());
        assertEquals(BigDecimal.valueOf(700.00), repository.findById("A2").getBalance());
    }

    @Test
    void shouldTransferExactAmountLeavingZeroBalance() {
        moneyTransferService.transfer("A1", "A2", BigDecimal.valueOf(1000.00));
        assertEquals(BigDecimal.valueOf(0.00), repository.findById("A1").getBalance());
        assertEquals(BigDecimal.valueOf(1500.00), repository.findById("A2").getBalance());
    }

    @Test
    void shouldFailTransferWhenSourceAccountDoesNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> moneyTransferService.transfer("X", "A2", BigDecimal.valueOf(100.00)));
    }

    @Test
    void shouldFailTransferWhenDestinationAccountDoesNotExist() {
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
    void shouldTransferMoneyCorrectlyWhenMultipleConcurrentTransactionsOccur() throws InterruptedException {
        final int numberOfThreads = 10;
        try (ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads)) {
            for (int i = 0; i < numberOfThreads; i++) {
                executorService.submit(() -> moneyTransferService.transfer("A1", "A2", BigDecimal.valueOf(10.00)));
            }
            executorService.shutdown();
            boolean isTerminated = executorService.awaitTermination(5, TimeUnit.SECONDS);
            System.out.println("Executor Service is terminated: " + isTerminated);
        }
        // The result will also depend on the rate limiting of 5 transactions per second
        assertEquals(BigDecimal.valueOf(1000.00 - 10.00 * 5), repository.findById("A1").getBalance());
        assertEquals(BigDecimal.valueOf(500.00 + 10.00 * 5), repository.findById("A2").getBalance());
    }

    @Test
    void shouldPreventDeadlocksDuringConcurrentTransfers() throws InterruptedException {
        repository.addAccount(new Account("A3", BigDecimal.valueOf(300.00)));
        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            for (int i = 0; i < 50; i++) {
                int finalI = i;
                executorService.submit(() -> {
                    if (finalI % 2 == 0) {
                        moneyTransferService.transfer("A1", "A2", BigDecimal.valueOf(5));
                    } else {
                        moneyTransferService.transfer("A2", "A1", BigDecimal.valueOf(5));
                    }
                });
            }
            executorService.shutdown();
            boolean isTerminated = executorService.awaitTermination(5, TimeUnit.SECONDS);
            System.out.println("Executor Service is terminated: " + isTerminated);
            // Sum must remain constant
            BigDecimal total = repository.findById("A1").getBalance().add(repository.findById("A2").getBalance());
            assertEquals(total, BigDecimal.valueOf(1500.00));
        }
    }

    @Test
    void shouldMaintainCorrectAccountBalancedAfterConcurrentTransfers() throws InterruptedException {
        try (final ExecutorService executorService = Executors.newFixedThreadPool(4)) {
            final Runnable transfer1 = () -> moneyTransferService.transfer("A1", "A2", BigDecimal.valueOf(100.00));
            final Runnable transfer2 = () -> moneyTransferService.transfer("A2", "A1", BigDecimal.valueOf(50.00));

            for (int i = 0; i < 10; i++) {
                executorService.submit(transfer1);
                executorService.submit(transfer2);
            }
            executorService.shutdown();
            boolean isTerminated = executorService.awaitTermination(5, TimeUnit.SECONDS);
            System.out.println("Executor Service is terminated: " + isTerminated);

            final BigDecimal total = repository.findById("A1").getBalance().add(repository.findById("A2").getBalance());
            assertEquals(BigDecimal.valueOf(1500.00), total);
        }
    }

    @Test
    void shouldRejectTransfersWithInvalidAccountIds() {
        assertThrows(IllegalArgumentException.class,
                () -> moneyTransferService.transfer("Invalid", "A2", BigDecimal.valueOf(100.00)));
        assertThrows(IllegalArgumentException.class,
                () -> moneyTransferService.transfer("A1", "Invalid", BigDecimal.valueOf(100.00)));
    }

    @Test
    void shouldRejectTransferWithZeroAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> moneyTransferService.transfer("A1", "A2", BigDecimal.ZERO));
    }
}