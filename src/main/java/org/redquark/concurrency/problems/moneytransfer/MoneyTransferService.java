package org.redquark.concurrency.problems.moneytransfer;

import org.redquark.concurrency.problems.moneytransfer.domains.Account;
import org.redquark.concurrency.problems.moneytransfer.logger.TransferLogger;
import org.redquark.concurrency.problems.moneytransfer.repositories.AccountRepository;
import org.redquark.concurrency.problems.moneytransfer.utils.LockManager;
import org.redquark.concurrency.problems.moneytransfer.utils.RateLimiterManager;
import org.redquark.concurrency.problems.moneytransfer.validators.TransferValidator;

import java.math.BigDecimal;
import java.util.List;

public class MoneyTransferService {

    private final AccountRepository accountRepository;
    private final LockManager lockManager;
    private final List<TransferValidator> validators;
    private final RateLimiterManager rateLimiter;
    private final TransferLogger logger;

    public MoneyTransferService(AccountRepository accountRepository, LockManager lockManager, List<TransferValidator> validators, RateLimiterManager rateLimiter, TransferLogger logger) {
        this.accountRepository = accountRepository;
        this.lockManager = lockManager;
        this.validators = validators;
        this.rateLimiter = rateLimiter;
        this.logger = logger;
    }

    public void transfer(String fromId, String toId, BigDecimal amount) {
        // Check rate limit
        rateLimiter.checkRateLimit(fromId);
        // Get account references
        final Account from = accountRepository.findById(fromId);
        final Account to = accountRepository.findById(toId);
        if (from == null || to == null) {
            throw new IllegalArgumentException("Account doesn't exist");
        }
        // Lock both accounts for consistency
        lockManager.lockBoth(from, to);
        boolean success = false;
        String reason = "OK";

        try {
            for (TransferValidator validator : validators) {
                validator.validate(from, to, amount);
            }
            from.debit(amount);
            to.credit(amount);
            success = true;
        } catch (Exception e) {
            reason = e.getMessage();
            throw e;
        } finally {
            lockManager.unlockBoth(from, to);
            logger.log(fromId, toId, amount, success, reason);
        }
    }
}
