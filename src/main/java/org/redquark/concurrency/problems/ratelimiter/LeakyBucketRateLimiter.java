package org.redquark.concurrency.problems.ratelimiter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LeakyBucketRateLimiter implements RateLimiter {

    private final int capacity;
    private final Queue<Long> bucket;

    public LeakyBucketRateLimiter(int capacity, int leakRateInSeconds) {
        this.capacity = capacity;
        this.bucket = new LinkedList<>();
        try (ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1)) {
            scheduledExecutorService.scheduleAtFixedRate(this::leakRequests, 0, leakRateInSeconds, TimeUnit.SECONDS);
        }
    }

    @Override
    public boolean isRequestAllowed(String userId) {
        long currentTime = System.currentTimeMillis();
        if (this.bucket.size() < this.capacity) {
            this.bucket.offer(currentTime);
            return true;
        }
        return false;
    }

    private synchronized void leakRequests() {
        if (!this.bucket.isEmpty()) {
            this.bucket.remove();
        }
    }
}
