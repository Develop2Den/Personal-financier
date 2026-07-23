package com.d2d.personal_financier.repository;

import com.d2d.personal_financier.entity.RefreshToken;
import com.d2d.personal_financier.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findAllByOwner(User owner);
    void deleteByExpiryDateBefore(LocalDateTime dateTime);

    @Modifying
    @Query("""
        update RefreshToken token
        set token.revokedAt = :revokedAt,
            token.lastUsedAt = :lastUsedAt
        where token.tokenHash = :tokenHash
          and token.revokedAt is null
        """)
    int revokeIfNotRevoked(
        @Param("tokenHash") String tokenHash,
        @Param("revokedAt") LocalDateTime revokedAt,
        @Param("lastUsedAt") LocalDateTime lastUsedAt
    );
}
