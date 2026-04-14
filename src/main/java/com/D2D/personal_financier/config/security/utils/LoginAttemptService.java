package com.D2D.personal_financier.config.security.utils;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPT = 5;
    private static final int BLOCK_MINUTES = 10;

    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> blockedUntil = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attempts.remove(username);
        blockedUntil.remove(username);
    }

    public void loginFailed(String username) {

        int attempt = attempts.getOrDefault(username, 0) + 1;
        attempts.put(username, attempt);

        if (attempt >= MAX_ATTEMPT) {
            blockedUntil.put(username, LocalDateTime.now().plusMinutes(BLOCK_MINUTES));
        }
    }

    public boolean isBlocked(String username) {

        LocalDateTime blockedTime = blockedUntil.get(username);

        if (blockedTime == null) {
            return false;
        }

        if (blockedTime.isBefore(LocalDateTime.now())) {
            blockedUntil.remove(username);
            attempts.remove(username);
            return false;
        }

        return true;
    }
}
