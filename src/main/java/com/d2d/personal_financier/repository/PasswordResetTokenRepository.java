package com.d2d.personal_financier.repository;

import com.d2d.personal_financier.entity.PasswordResetToken;
import com.d2d.personal_financier.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByOwner(User owner);
    void deleteByExpiryDateBefore(LocalDateTime dateTime);
}
