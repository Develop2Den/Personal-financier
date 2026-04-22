package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.jwt.JwtProvider;
import com.d2d.personal_financier.dto.authDTO.AuthResponseDto;
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

import java.time.LocalDateTime;
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
            .token("old-refresh-token")
            .owner(user)
            .expiryDate(LocalDateTime.now().plusMinutes(10))
            .build();

        when(refreshTokenRepository.findByToken("old-refresh-token")).thenReturn(Optional.of(currentToken));
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
            .token("revoked-token")
            .owner(user)
            .expiryDate(LocalDateTime.now().plusMinutes(10))
            .revokedAt(LocalDateTime.now().minusMinutes(1))
            .build();

        when(refreshTokenRepository.findByToken("revoked-token")).thenReturn(Optional.of(revokedToken));

        assertThrows(InvalidRefreshTokenException.class, () -> refreshTokenService.refresh("revoked-token"));
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }
}
