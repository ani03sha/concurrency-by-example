package org.redquark.concurrency.problems.moneytransfer.validators;

import org.redquark.concurrency.problems.moneytransfer.domains.Account;

import java.math.BigDecimal;

public class PositiveAmountValidator implements TransferValidator{

    @Override
    public void validate(Account from, Account to, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
