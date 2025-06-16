package org.redquark.concurrency.problems.ratelimiter;

public interface RateLimiter {

    boolean isRequestAllowed(String userId);
}
