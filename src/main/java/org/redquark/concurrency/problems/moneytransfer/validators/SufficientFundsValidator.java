package org.redquark.concurrency.problems.moneytransfer.validators;

import org.redquark.concurrency.problems.moneytransfer.domains.Account;

import java.math.BigDecimal;

public class SufficientFundsValidator implements TransferValidator{
    @Override
    public void validate(Account from, Account to, BigDecimal amount) {
        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient Balance");
        }
    }
}
