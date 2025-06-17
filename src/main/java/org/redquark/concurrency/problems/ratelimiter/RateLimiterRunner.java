package org.redquark.concurrency.problems.ratelimiter;

import static org.redquark.concurrency.problems.ratelimiter.RateLimitingAlgorithms.FIXED_WINDOW;
import static org.redquark.concurrency.problems.ratelimiter.RateLimitingAlgorithms.LEAKY_BUCKET;
import static org.redquark.concurrency.problems.ratelimiter.RateLimitingAlgorithms.SLIDING_WINDOW;
import static org.redquark.concurrency.problems.ratelimiter.RateLimitingAlgorithms.TOKEN_BUCKET;

public class RateLimiterRunner {

    public static void main(String[] args) throws InterruptedException {
        final RateLimiterService rateLimiterService = new RateLimiterService();

        rateLimiterService.registerUser("user_1", FIXED_WINDOW, 5, 10);
        rateLimiterService.registerUser("user_2", SLIDING_WINDOW, 3, 5);
        rateLimiterService.registerUser("user_3", TOKEN_BUCKET, 5, 10);
        rateLimiterService.registerUser("user_4", LEAKY_BUCKET, 3, 4);

        for (int i = 0; i < 7; i++) {
            System.out.println("User 1 Request " + (i + 1) + " : " + rateLimiterService.isRequestAllowed("user_1"));
            System.out.println("User 2 Request " + (i + 1) + " : " + rateLimiterService.isRequestAllowed("user_2"));
            System.out.println("User 3 Request " + (i + 1) + " : " + rateLimiterService.isRequestAllowed("user_3"));
            System.out.println("User 4 Request " + (i + 1) + " : " + rateLimiterService.isRequestAllowed("user_4"));
            Thread.sleep(1000);
        }
    }
}
