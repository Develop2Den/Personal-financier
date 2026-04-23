package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.jwt.JwtProvider;
import com.d2d.personal_financier.dto.auth_dto.AuthResponseDto;
import com.d2d.personal_financier.entity.RefreshToken;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.exception.InvalidRefreshTokenException;
import com.d2d.personal_financier.exception.RefreshTokenExpiredException;
import com.d2d.personal_financier.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final AuditService auditService;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    public AuthResponseDto createTokenPair(User user) {

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshTokenValue = UUID.randomUUID().toString();

        refreshTokenRepository.save(
            RefreshToken.builder()
                .token(refreshTokenValue)
                .owner(user)
                .expiryDate(LocalDateTime.now().plusNanos(refreshExpirationMs * 1_000_000))
                .build()
        );

        return new AuthResponseDto(accessToken, refreshTokenValue);
    }

    public AuthResponseDto refresh(String refreshTokenValue) {

        RefreshToken currentToken = refreshTokenRepository.findByToken(refreshTokenValue)
            .orElseThrow(() -> {
                auditService.log("TOKEN_REFRESH", "FAILED", null, null, "Unknown refresh token");
                return new InvalidRefreshTokenException();
            });

        if (currentToken.isRevoked()) {
            auditService.log("TOKEN_REFRESH", "FAILED", currentToken.getOwner(), currentToken.getOwner().getUsername(), "Revoked refresh token reuse");
            throw new InvalidRefreshTokenException();
        }

        if (currentToken.isExpired()) {
            refreshTokenRepository.delete(currentToken);
            auditService.log("TOKEN_REFRESH", "FAILED", currentToken.getOwner(), currentToken.getOwner().getUsername(), "Expired refresh token");
            throw new RefreshTokenExpiredException();
        }

        currentToken.setRevokedAt(LocalDateTime.now());
        currentToken.setLastUsedAt(LocalDateTime.now());

        User user = currentToken.getOwner();
        AuthResponseDto response = createTokenPair(user);

        auditService.log("TOKEN_REFRESH", "SUCCESS", user, user.getUsername(), "Refresh token rotated");

        return response;
    }

    public void revoke(String refreshTokenValue) {

        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            return;
        }

        refreshTokenRepository.findByToken(refreshTokenValue)
            .ifPresent(token -> {
                if (!token.isRevoked()) {
                    token.setRevokedAt(LocalDateTime.now());
                }
            });
    }

    public void revokeAllForUser(User user) {

        for (RefreshToken token : refreshTokenRepository.findAllByOwner(user)) {
            if (!token.isRevoked()) {
                token.setRevokedAt(LocalDateTime.now());
            }
        }
    }

    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
