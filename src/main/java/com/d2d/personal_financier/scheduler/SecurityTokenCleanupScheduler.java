package com.d2d.personal_financier.scheduler;

import com.d2d.personal_financier.service.PasswordResetService;
import com.d2d.personal_financier.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityTokenCleanupScheduler {

    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;

    @Scheduled(cron = "0 15 * * * *")
    public void deleteExpiredTokens() {
        refreshTokenService.deleteExpiredTokens();
        passwordResetService.deleteExpiredTokens();
    }
}
