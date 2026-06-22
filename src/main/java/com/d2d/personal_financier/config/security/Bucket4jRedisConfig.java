package com.d2d.personal_financier.config.security;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Configuration
public class Bucket4jRedisConfig {

    @Bean
    public ProxyManager<byte[]> proxyManager(
        LettuceConnectionFactory lettuceConnectionFactory) {

        RedisClient redisClient =
            RedisClient.create(
                "redis://"
                    + lettuceConnectionFactory.getHostName()
                    + ":"
                    + lettuceConnectionFactory.getPort());

        return LettuceBasedProxyManager
            .builderFor(redisClient)
            .withExpirationStrategy(
                ExpirationAfterWriteStrategy
                    .basedOnTimeForRefillingBucketUpToMax(
                        Duration.ofHours(1)
                    )
            )
            .build();
    }
}
