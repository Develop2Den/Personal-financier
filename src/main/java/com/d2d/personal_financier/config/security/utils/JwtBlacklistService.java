package com.d2d.personal_financier.config.security.utils;

import com.d2d.personal_financier.config.security.jwt.JwtProvider;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProvider jwtProvider;

    public void blacklistToken(String token) {

        if (token == null || token.isBlank()) {
            return;
        }

        try {

            long ttl =
                jwtProvider.getExpirationDate(token).getTime()
                    - System.currentTimeMillis();

            if (ttl > 0) {
                redisTemplate.opsForValue()
                    .set(token, "blacklisted", ttl, TimeUnit.MILLISECONDS);
            }

        } catch (JwtException | IllegalArgumentException e) {

            log.debug("Unable to blacklist token: {}", e.getMessage());

        }
    }

    public boolean isBlacklisted(String token) {

        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(token));
        } catch (Exception e) {
            log.warn("Redis unavailable while checking blacklist");
            return false;
        }
    }
}
