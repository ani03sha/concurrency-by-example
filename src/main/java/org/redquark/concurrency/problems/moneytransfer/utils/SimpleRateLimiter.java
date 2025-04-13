package org.redquark.concurrency.problems.moneytransfer.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class SimpleRateLimiter {

    private final int maxTokens;
    private final long refillInterval;
    private final AtomicInteger tokens;
    private volatile long lastRefillTimestamp;

    public SimpleRateLimiter(int maxTokens, long refillInterval) {
        this.maxTokens = maxTokens;
        this.refillInterval = refillInterval;
        this.tokens = new AtomicInteger(maxTokens);
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    public synchronized boolean tryAcquire() {
        refill();
        if (this.tokens.get() > 0) {
            this.tokens.decrementAndGet();
            return true;
        }
        return false;
    }

    public void refill() {
        final long now = System.currentTimeMillis();
        if (now - this.lastRefillTimestamp >= this.refillInterval) {
            this.tokens.set(this.maxTokens);
            this.lastRefillTimestamp = now;
        }
    }
}
