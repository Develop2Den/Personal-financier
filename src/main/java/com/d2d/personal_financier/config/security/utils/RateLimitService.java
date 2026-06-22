package com.d2d.personal_financier.config.security.utils;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private static final String LOGIN_PATH = "/auth/login";
    private static final String REGISTER_PATH = "/auth/register";
    private static final String REFRESH_PATH = "/auth/refresh";

    private final ProxyManager<byte[]> proxyManager;

    public Bucket resolveBucket(String ip, String path) {

        RateLimitPolicy policy = resolvePolicy(path);

        String bucketKey = ip + ":" + policy.name();

        return proxyManager.builder()
            .build(
                bucketKey.getBytes(StandardCharsets.UTF_8),
                () -> createConfiguration(policy)
            );
    }

    private BucketConfiguration createConfiguration(RateLimitPolicy policy) {

        Bandwidth limit = Bandwidth.builder()
            .capacity(policy.capacity())
            .refillGreedy(policy.capacity(), policy.refillPeriod())
            .build();

        return BucketConfiguration.builder()
            .addLimit(limit)
            .build();
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
