package com.D2D.personal_financier.scheduler;

import com.D2D.personal_financier.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EmailTokenCleanupScheduler {

    private final EmailVerificationTokenRepository tokenRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void deleteExpiredTokens() {

        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());

    }
}
