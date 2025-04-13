package org.redquark.concurrency.problems.moneytransfer.validators;

import org.redquark.concurrency.problems.moneytransfer.domains.Account;

import java.math.BigDecimal;

public class SameAccountValidator implements TransferValidator{

    @Override
    public void validate(Account from, Account to, BigDecimal amount) {
        if (from.getId().equals(to.getId())) {
            throw new IllegalArgumentException("Sender and receiver cannot be same");
        }
    }
}
