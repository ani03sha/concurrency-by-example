package org.redquark.concurrency.problems.moneytransfer.repositories;

import org.redquark.concurrency.problems.moneytransfer.domains.Account;

public interface AccountRepository {
    Account findById(String id);
}
