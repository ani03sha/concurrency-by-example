package org.redquark.concurrency.problems.ratelimiter;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SlidingWindowRateLimiter implements RateLimiter {

    private final int maxRequests;
    private final long windowSizeMillis;
    private final Map<String, Deque<Long>> requestLogs;

    public SlidingWindowRateLimiter(int maxRequests, long windowSizeMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
        this.requestLogs = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized boolean isRequestAllowed(String userId) {
        final long currentTime = System.currentTimeMillis();
        this.requestLogs.putIfAbsent(userId, new ConcurrentLinkedDeque<>());
        final Deque<Long> timestamps = this.requestLogs.get(userId);
        while (!timestamps.isEmpty() && currentTime - timestamps.peek() >= this.windowSizeMillis) {
            timestamps.removeFirst();
        }
        if (timestamps.size() < this.maxRequests) {
            timestamps.add(currentTime);
            return true;
        }
        return false;
    }
}
