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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
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

    private static final String TOKEN_REFRESH = "TOKEN_REFRESH";
    private static final String FAILED = "FAILED";

    public AuthResponseDto createTokenPair(User user) {

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshTokenValue = UUID.randomUUID().toString();

        refreshTokenRepository.save(
            RefreshToken.builder()
                .tokenHash(hashToken(refreshTokenValue))
                .owner(user)
                .expiryDate(LocalDateTime.now().plus(Duration.ofMillis(refreshExpirationMs)))
                .build()
        );

        return new AuthResponseDto(accessToken, refreshTokenValue);
    }

    public AuthResponseDto refresh(String refreshTokenValue) {

        RefreshToken currentToken = refreshTokenRepository.findByTokenHash(hashToken(refreshTokenValue))
            .orElseThrow(() -> {
                auditService.log(TOKEN_REFRESH, FAILED, null, null, "Unknown refresh token");
                return new InvalidRefreshTokenException();
            });

        if (currentToken.isRevoked()) {
            auditService.log(TOKEN_REFRESH, FAILED, currentToken.getOwner(), currentToken.getOwner().getUsername(), "Revoked refresh token reuse");
            throw new InvalidRefreshTokenException();
        }

        if (currentToken.isExpired()) {
            refreshTokenRepository.delete(currentToken);
            auditService.log(TOKEN_REFRESH, FAILED, currentToken.getOwner(), currentToken.getOwner().getUsername(), "Expired refresh token");
            throw new RefreshTokenExpiredException();
        }

        currentToken.setRevokedAt(LocalDateTime.now());
        currentToken.setLastUsedAt(LocalDateTime.now());

        User user = currentToken.getOwner();
        AuthResponseDto response = createTokenPair(user);

        auditService.log(TOKEN_REFRESH, "SUCCESS", user, user.getUsername(), "Refresh token rotated");

        return response;
    }

    public void revoke(String refreshTokenValue) {

        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            return;
        }

        refreshTokenRepository.findByTokenHash(hashToken(refreshTokenValue))
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

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available", e);
        }
    }
}
