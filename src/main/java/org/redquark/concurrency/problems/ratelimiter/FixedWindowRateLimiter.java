package org.redquark.concurrency.problems.ratelimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowRateLimiter implements RateLimiter {

    private final int maxRequests;
    private final long windowSizeMillis;
    private final Map<String, Integer> requestCounts;
    private long windowStart;

    public FixedWindowRateLimiter(int maxRequests, long windowSizeMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
        this.requestCounts = new ConcurrentHashMap<>();
        this.windowStart = System.currentTimeMillis();
    }

    @Override
    public synchronized boolean isRequestAllowed(String userId) {
        final long currentTime = System.currentTimeMillis();
        if (currentTime - this.windowStart >= this.windowSizeMillis) {
            this.requestCounts.remove(userId);
            this.windowStart = currentTime;
        }
        this.requestCounts.put(userId, requestCounts.getOrDefault(userId, 0) + 1);
        return this.requestCounts.get(userId) <= this.maxRequests;
    }
}
