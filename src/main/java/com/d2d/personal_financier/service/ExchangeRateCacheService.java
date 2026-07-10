package com.d2d.personal_financier.service;

import com.d2d.personal_financier.dto.currency_dto.ExchangeRateDto;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class ExchangeRateCacheService {

    private static final String KEY_PREFIX = "exchange_rates:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public ExchangeRateCacheService(
        @Qualifier("currencyRedisTemplate")
        RedisTemplate<String, Object> redisTemplate,
        ObjectMapper objectMapper) {

        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveRates(
        ExchangeRateSource source,
        List<ExchangeRateDto> rates,
        Duration ttl) {

        redisTemplate.opsForValue().set(
            buildKey(source),
            rates,
            ttl
        );
    }

    public List<ExchangeRateDto> getRates(
        ExchangeRateSource source) {

        Object value = redisTemplate.opsForValue().get(buildKey(source));

        return value == null
            ? List.of()
            : objectMapper.convertValue(
            value,
            new TypeReference<List<ExchangeRateDto>>() {
            }
        );
    }

    public void evictRates(ExchangeRateSource source) {

        redisTemplate.delete(buildKey(source));
    }

    private String buildKey(ExchangeRateSource source) {
        return KEY_PREFIX + source.name();
    }
}
