package com.backend.distributedqueue.validation;

import com.backend.distributedqueue.exception.JobActivityException;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    // Make the limit configurable via application.properties
    @Value("${rate-limiter.limit:10}") // Default to 10 if not set
    private long limit;

    // The fixed window duration in seconds
    private static final long WINDOW_IN_SECONDS = 60;

    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Checks if a request is allowed for a given user. Throws an exception if the limit is exceeded.
     *
     * @param userId The user's unique identifier.
     * @param userIp The user's IP address.
     * @throws JobActivityException if the number of requests exceeds the configured limit.
     */
    public void checkRateLimit(String userId, String userIp) {
        String clientKey = generateClientKey(userId, userIp);
        Long currentCount = redisTemplate.opsForValue().increment(clientKey);

        if (currentCount == null) {
            // Should not happen with increment, but good practice to handle
            throw new IllegalStateException("Could not retrieve count from Redis.");
        }

        // If it's the first request in the window, set the 60-second expiry
        if (currentCount == 1) {
            redisTemplate.expire(clientKey, WINDOW_IN_SECONDS, TimeUnit.SECONDS);
        }

        // If the count exceeds the limit, throw the exception
        if (currentCount > limit) {
            throw new JobActivityException(
                    String.format("Rate limit of %d requests per %d seconds exceeded.", limit, WINDOW_IN_SECONDS)
            );
        }
    }

    /**
     * Generates a SHA-256 hash from the userId and userIp to use as a Redis key.
     */
    private String generateClientKey(String userId, String userIp) {
        String rawId = userId + userIp;
        return Hashing.sha256()
                .hashString(rawId, StandardCharsets.UTF_8)
                .toString();
    }
}
