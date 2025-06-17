package org.redquark.concurrency.problems.ratelimiter;

public class RateLimiterFactory {

    public static RateLimiter createRateLimiter(RateLimitingAlgorithms algorithm, int maxRequests, long windowSizeMillis) {
        return switch (algorithm) {
            case FIXED_WINDOW -> new FixedWindowRateLimiter(maxRequests, windowSizeMillis);
            case SLIDING_WINDOW -> new SlidingWindowRateLimiter(maxRequests, windowSizeMillis);
            case TOKEN_BUCKET -> new TokenBucketRateLimiter(maxRequests, (int) (1.0 * maxRequests / windowSizeMillis * 1000));
            case LEAKY_BUCKET-> new LeakyBucketRateLimiter(maxRequests, (int) (1.0 * maxRequests / windowSizeMillis * 1000));
        };
    }
}
