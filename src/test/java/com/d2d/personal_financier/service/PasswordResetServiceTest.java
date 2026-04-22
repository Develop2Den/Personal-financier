package com.d2d.personal_financier.service;

import com.d2d.personal_financier.dto.authDTO.PasswordResetConfirmDto;
import com.d2d.personal_financier.dto.message.MessageResponseDto;
import com.d2d.personal_financier.entity.PasswordResetToken;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.repository.PasswordResetTokenRepository;
import com.d2d.personal_financier.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Test
    void requestResetShouldReturnGenericMessageForUnknownEmail() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        MessageResponseDto response = passwordResetService.requestReset("missing@example.com");

        assertEquals(
            "If an account with this email exists, password reset instructions have been sent.",
            response.message()
        );
        verify(emailService, never()).sendPasswordResetEmail(any(), any());
    }

    @Test
    void confirmResetShouldUpdatePasswordAndRevokeAllRefreshTokens() {
        User user = User.builder()
            .id(11L)
            .username("denisdev")
            .password("old-hash")
            .build();

        PasswordResetToken token = PasswordResetToken.builder()
            .token("reset-token")
            .owner(user)
            .expiryDate(LocalDateTime.now().plusMinutes(30))
            .build();

        when(passwordResetTokenRepository.findByToken("reset-token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("MyPass123!")).thenReturn("new-hash");

        MessageResponseDto response = passwordResetService.confirmReset(
            new PasswordResetConfirmDto("reset-token", "MyPass123!")
        );

        assertEquals("Password has been reset successfully.", response.message());
        assertEquals("new-hash", user.getPassword());
        assertTrue(token.isUsed());
        verify(userRepository).save(user);
        verify(refreshTokenService).revokeAllForUser(user);
        verify(auditService).log(
            "PASSWORD_RESET_CONFIRM",
            "SUCCESS",
            user,
            user.getUsername(),
            "Password reset completed and refresh tokens revoked"
        );
    }
}
