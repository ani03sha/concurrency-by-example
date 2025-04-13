package org.redquark.concurrency.problems.moneytransfer.utils;

import org.redquark.concurrency.problems.moneytransfer.domains.Account;

public class LockManager {

    public void lockBoth(Account from, Account to) {
        // Always lock in a consistent order to prevent deadlocks
        if (from.getId().compareTo(to.getId()) < 0) {
            from.getLock().lock();
            to.getLock().lock();
        } else {
            to.getLock().lock();
            from.getLock().lock();
        }
    }

    public void unlockBoth(Account from, Account to) {
        from.getLock().unlock();
        to.getLock().unlock();
    }
}
