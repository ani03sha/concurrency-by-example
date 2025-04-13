package org.redquark.concurrency.problems.moneytransfer.validators;

import org.redquark.concurrency.problems.moneytransfer.domains.Account;

import java.math.BigDecimal;

public interface TransferValidator {

    void validate(Account from, Account to, BigDecimal amount);
}
