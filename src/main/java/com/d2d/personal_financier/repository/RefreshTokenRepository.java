package com.d2d.personal_financier.repository;

import com.d2d.personal_financier.entity.RefreshToken;
import com.d2d.personal_financier.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findAllByOwner(User owner);
    void deleteByExpiryDateBefore(LocalDateTime dateTime);
}
