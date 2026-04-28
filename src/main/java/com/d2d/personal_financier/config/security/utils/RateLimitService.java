package com.d2d.personal_financier.config.security.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService {

    private static final String LOGIN_PATH = "/auth/login";
    private static final String REGISTER_PATH = "/auth/register";
    private static final String REFRESH_PATH = "/auth/refresh";

    private final Cache<String, Bucket> buckets = Caffeine.newBuilder()
        .expireAfterAccess(Duration.ofHours(1))
        .maximumSize(10_000)
        .build();

    private Bucket createNewBucket(RateLimitPolicy policy) {

        Bandwidth limit = Bandwidth.builder()
            .capacity(policy.capacity())
            .refillGreedy(policy.capacity(), policy.refillPeriod())
            .build();

        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    public Bucket resolveBucket(String ip, String path) {
        RateLimitPolicy policy = resolvePolicy(path);
        String bucketKey = ip + ":" + policy.name();

        return buckets.get(bucketKey, key -> createNewBucket(policy));
    }

    private RateLimitPolicy resolvePolicy(String path) {
        return switch (path) {
            case LOGIN_PATH -> RateLimitPolicy.LOGIN;
            case REGISTER_PATH -> RateLimitPolicy.REGISTER;
            case REFRESH_PATH -> RateLimitPolicy.REFRESH;
            default -> RateLimitPolicy.API;
        };
    }

    private enum RateLimitPolicy {
        LOGIN(5, Duration.ofMinutes(1)),
        REGISTER(3, Duration.ofMinutes(1)),
        REFRESH(10, Duration.ofMinutes(1)),
        API(100, Duration.ofMinutes(1));

        private final long capacity;
        private final Duration refillPeriod;

        RateLimitPolicy(long capacity, Duration refillPeriod) {
            this.capacity = capacity;
            this.refillPeriod = refillPeriod;
        }

        public long capacity() {
            return capacity;
        }

        public Duration refillPeriod() {
            return refillPeriod;
        }
    }
}
