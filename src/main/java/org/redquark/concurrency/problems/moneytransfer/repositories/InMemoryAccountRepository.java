package org.redquark.concurrency.problems.moneytransfer.repositories;

import org.redquark.concurrency.problems.moneytransfer.domains.Account;

import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAccountRepository implements AccountRepository {

    private final ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();

    public void addAccount(Account account) {
        this.accounts.put(account.getId(), account);
    }

    @Override
    public Account findById(String id) {
        final Account account = accounts.get(id);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + id);
        }
        return account;
    }
}
