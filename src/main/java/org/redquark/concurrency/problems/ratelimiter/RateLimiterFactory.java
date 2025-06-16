package org.redquark.concurrency.problems.ratelimiter;

public class RateLimiterFactory {

    public static RateLimiter createRateLimiter(String type, int maxRequests, long windowSizeMillis) {
        return switch (type) {
            case "FIXED" -> new FixedWindowRateLimiter(maxRequests, windowSizeMillis);
            case "SLIDING" -> new SlidingWindowRateLimiter(maxRequests, windowSizeMillis);
            case "TOKEN_BUCKET" -> new TokenBucketRateLimiter(maxRequests, (int) (1.0 * maxRequests / windowSizeMillis * 1000));
            case "LEAKY_BUCKET" -> new LeakyBucketRateLimiter(maxRequests, (int) (1.0 * maxRequests / windowSizeMillis * 1000));
            default -> throw new IllegalArgumentException("Unsupported type");
        };
    }
}
