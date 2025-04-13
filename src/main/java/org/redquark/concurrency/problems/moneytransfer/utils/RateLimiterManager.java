package org.redquark.concurrency.problems.moneytransfer.utils;

import java.util.concurrent.ConcurrentHashMap;

public class RateLimiterManager {

    private final ConcurrentHashMap<String, SimpleRateLimiter> rateLimiters = new ConcurrentHashMap<>();

    public void checkRateLimit(String accountId) {
        // 5 tokens per second
        final SimpleRateLimiter limiter = rateLimiters.computeIfAbsent(accountId, _ -> new SimpleRateLimiter(5, 1000));
        if (!limiter.tryAcquire()) {
            throw new IllegalArgumentException("Rate limit exceeded for account: " + accountId);
        }
    }
}
