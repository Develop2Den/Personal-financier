package com.d2d.personal_financier.repository;

import com.d2d.personal_financier.entity.EmailVerificationToken;
import com.d2d.personal_financier.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    Optional<EmailVerificationToken> findByOwner(User owner);
    void deleteByOwner(User owner);
    void deleteByExpiryDateBefore(LocalDateTime dateTime);
}
