package org.redquark.concurrency.problems.ratelimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiterService {

    private final Map<String, RateLimiter> userRateLimiters;

    public RateLimiterService() {
        this.userRateLimiters = new ConcurrentHashMap<>();
    }

    public void registerUser(String userId, RateLimitingAlgorithms algorithm, int mazRequests, int windowSizeInMillis) {
        userRateLimiters.put(userId, RateLimiterFactory.createRateLimiter(algorithm, mazRequests, windowSizeInMillis));
    }

    public boolean isRequestAllowed(String userId) {
        final RateLimiter rateLimiter = this.userRateLimiters.get(userId);
        if (rateLimiter == null) {
            throw new IllegalArgumentException("User is not registered with a rate limiter");
        }
        return rateLimiter.isRequestAllowed(userId);
    }
}
