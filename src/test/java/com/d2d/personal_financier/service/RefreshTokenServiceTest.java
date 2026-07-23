package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.jwt.JwtProvider;
import com.d2d.personal_financier.dto.auth_dto.AuthResponseDto;
import com.d2d.personal_financier.entity.RefreshToken;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.exception.InvalidRefreshTokenException;
import com.d2d.personal_financier.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshExpirationMs", 604800000L);
    }

    @Test
    void refreshShouldRotateTokenPairAndRevokePreviousToken() {
        User user = User.builder()
            .id(1L)
            .username("denisdev")
            .build();

        RefreshToken currentToken = RefreshToken.builder()
            .tokenHash(hashToken("old-refresh-token"))
            .owner(user)
            .expiryDate(LocalDateTime.now().plusMinutes(10))
            .build();

        when(refreshTokenRepository.findByTokenHash(hashToken("old-refresh-token"))).thenReturn(Optional.of(currentToken));
        when(refreshTokenRepository.revokeIfNotRevoked(eq(hashToken("old-refresh-token")), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(1);
        when(jwtProvider.generateAccessToken(user)).thenReturn("new-access-token");

        AuthResponseDto response = refreshTokenService.refresh("old-refresh-token");

        assertEquals("new-access-token", response.token());
        assertNotNull(response.refreshToken());
        assertNotEquals("old-refresh-token", response.refreshToken());
        assertNotNull(currentToken.getRevokedAt());
        assertNotNull(currentToken.getLastUsedAt());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        verify(auditService).log("TOKEN_REFRESH", "SUCCESS", user, user.getUsername(), "Refresh token rotated");
    }

    @Test
    void refreshShouldRejectRevokedToken() {
        User user = User.builder()
            .id(1L)
            .username("denisdev")
            .build();

        RefreshToken revokedToken = RefreshToken.builder()
            .tokenHash(hashToken("revoked-token"))
            .owner(user)
            .expiryDate(LocalDateTime.now().plusMinutes(10))
            .revokedAt(LocalDateTime.now().minusMinutes(1))
            .build();

        when(refreshTokenRepository.findByTokenHash(hashToken("revoked-token"))).thenReturn(Optional.of(revokedToken));

        assertThrows(InvalidRefreshTokenException.class, () -> refreshTokenService.refresh("revoked-token"));
        verify(refreshTokenRepository, never()).revokeIfNotRevoked(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void refreshShouldRejectTokenConsumedConcurrently() {
        User user = User.builder()
            .id(1L)
            .username("denisdev")
            .build();

        RefreshToken currentToken = RefreshToken.builder()
            .tokenHash(hashToken("racing-token"))
            .owner(user)
            .expiryDate(LocalDateTime.now().plusMinutes(10))
            .build();

        when(refreshTokenRepository.findByTokenHash(hashToken("racing-token"))).thenReturn(Optional.of(currentToken));
        when(refreshTokenRepository.revokeIfNotRevoked(eq(hashToken("racing-token")), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(0);

        assertThrows(InvalidRefreshTokenException.class, () -> refreshTokenService.refresh("racing-token"));

        assertNull(currentToken.getRevokedAt());
        assertNull(currentToken.getLastUsedAt());
        verify(jwtProvider, never()).generateAccessToken(any(User.class));
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
        verify(auditService).log("TOKEN_REFRESH", "FAILED", user, user.getUsername(), "Revoked refresh token reuse");
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
