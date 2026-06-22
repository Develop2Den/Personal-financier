package com.d2d.personal_financier.config.security.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final int MAX_ATTEMPT = 5;
    private static final int BLOCK_MINUTES = 10;

    private final RedisTemplate<String, String> redisTemplate;

    private String getKey(String username) {
        return "login:attempts:" + username;
    }

    public void loginSucceeded(String username) {

        try {

            redisTemplate.delete(getKey(username));

        } catch (Exception e) {

            log.warn("Redis unavailable while clearing login attempts for user: {}", username);

        }
    }

    public void loginFailed(String username) {

        try {

            String key = getKey(username);

            Long attempts = redisTemplate.opsForValue().increment(key);

            if (attempts != null && attempts == 1) {
                redisTemplate.expire(key, Duration.ofMinutes(BLOCK_MINUTES));
            }

        } catch (Exception e) {

            log.warn("Redis unavailable while recording failed login attempt for user: {}", username);

        }
    }

    public boolean isBlocked(String username) {

        try {

            String value = redisTemplate.opsForValue().get(getKey(username));

            if (value == null) {
                return false;
            }

            return Integer.parseInt(value) >= MAX_ATTEMPT;

        } catch (NumberFormatException e) {

            redisTemplate.delete(getKey(username));
            return false;

        } catch (Exception e) {

            log.warn("Redis unavailable while checking login attempts for user: {}", username);
            return false;

        }
    }

}

