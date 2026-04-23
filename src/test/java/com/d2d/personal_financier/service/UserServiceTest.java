package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.utils.LoginAttemptService;
import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.dto.auth_dto.AuthResponseDto;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.exception.EmailNotVerifiedException;
import com.d2d.personal_financier.repository.UserRepository;
import com.d2d.personal_financier.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private UserService userService;

    @Test
    void loginShouldReturnTokenPairForVerifiedUser() {
        User user = User.builder()
            .id(7L)
            .username("denisdev")
            .password("encoded")
            .verified(true)
            .build();

        AuthResponseDto expected = new AuthResponseDto("access-token", "refresh-token");

        when(loginAttemptService.isBlocked("denisdev")).thenReturn(false);
        when(userRepository.findByUsername("denisdev")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("MyPass123!", "encoded")).thenReturn(true);
        when(refreshTokenService.createTokenPair(user)).thenReturn(expected);

        AuthResponseDto response = userService.login("denisdev", "MyPass123!");

        assertEquals(expected, response);
        verify(loginAttemptService).loginSucceeded("denisdev");
    }

    @Test
    void loginShouldRejectUnverifiedUser() {
        User user = User.builder()
            .id(7L)
            .username("denisdev")
            .password("encoded")
            .verified(false)
            .build();

        when(loginAttemptService.isBlocked("denisdev")).thenReturn(false);
        when(userRepository.findByUsername("denisdev")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("MyPass123!", "encoded")).thenReturn(true);

        assertThrows(EmailNotVerifiedException.class, () -> userService.login("denisdev", "MyPass123!"));
        verify(refreshTokenService, never()).createTokenPair(any());
    }
}
