package org.redquark.concurrency.problems.ratelimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketRateLimiter implements RateLimiter {

    private final int capacity;
    private final double refillRate;
    private final Map<String, Integer> tokens;
    private final Map<String, Long> lastRefillTimestamp;

    public TokenBucketRateLimiter(int capacity, int refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = new ConcurrentHashMap<>();
        this.lastRefillTimestamp = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized boolean isRequestAllowed(String userId) {
        final long currentTime = System.currentTimeMillis();
        this.lastRefillTimestamp.putIfAbsent(userId, currentTime);
        this.tokens.putIfAbsent(userId, this.capacity);
        final long lastRefillTime = this.lastRefillTimestamp.get(userId);
        final long elapsedTime = (currentTime - lastRefillTime) / 1000;
        if (elapsedTime > 0) {
            final int newTokens = Math.min(this.capacity, this.tokens.get(userId) + (int) (elapsedTime * this.refillRate));
            this.tokens.put(userId, newTokens);
            this.lastRefillTimestamp.put(userId, currentTime);
        }
        if (this.tokens.get(userId) > 0) {
            this.tokens.put(userId, tokens.get(userId) - 1);
            return true;
        }
        return false;
    }
}
