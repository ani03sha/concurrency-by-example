package org.redquark.concurrency.problems.ratelimiter;

public enum RateLimitingAlgorithms {

    FIXED_WINDOW,
    SLIDING_WINDOW,
    TOKEN_BUCKET,
    LEAKY_BUCKET
}
