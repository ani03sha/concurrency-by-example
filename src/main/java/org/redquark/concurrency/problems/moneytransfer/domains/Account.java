package org.redquark.concurrency.problems.moneytransfer.domains;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {

    private final String id;
    private BigDecimal balance;
    private final Lock lock;

    public Account(String id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
        this.lock = new ReentrantLock();
    }

    public String getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Lock getLock() {
        return lock;
    }

    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}
