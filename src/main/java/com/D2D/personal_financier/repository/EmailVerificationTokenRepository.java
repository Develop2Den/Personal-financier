package com.D2D.personal_financier.repository;

import com.D2D.personal_financier.entity.EmailVerificationToken;
import com.D2D.personal_financier.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    void deleteByExpiryDateBefore(LocalDateTime dateTime);
}
