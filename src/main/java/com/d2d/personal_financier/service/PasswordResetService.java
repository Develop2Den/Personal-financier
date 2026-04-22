package com.d2d.personal_financier.service;

import com.d2d.personal_financier.dto.authDTO.PasswordResetConfirmDto;
import com.d2d.personal_financier.dto.message.MessageResponseDto;
import com.d2d.personal_financier.entity.PasswordResetToken;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.exception.InvalidPasswordResetTokenException;
import com.d2d.personal_financier.exception.PasswordResetTokenAlreadyUsedException;
import com.d2d.personal_financier.exception.PasswordResetTokenExpiredException;
import com.d2d.personal_financier.repository.PasswordResetTokenRepository;
import com.d2d.personal_financier.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;
    private final AuditService auditService;

    public MessageResponseDto requestReset(String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            auditService.log("PASSWORD_RESET_REQUEST", "IGNORED", null, maskEmail(email), "Password reset requested for unknown email");
            return genericResponse();
        }

        User user = optionalUser.get();

        passwordResetTokenRepository.deleteByOwner(user);

        String token = UUID.randomUUID().toString();

        passwordResetTokenRepository.save(
            PasswordResetToken.builder()
                .token(token)
                .owner(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build()
        );

        emailService.sendPasswordResetEmail(user.getEmail(), token);
        auditService.log("PASSWORD_RESET_REQUEST", "SUCCESS", user, user.getUsername(), "Password reset email sent");

        return genericResponse();
    }

    public MessageResponseDto confirmReset(PasswordResetConfirmDto request) {

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.token())
            .orElseThrow(() -> {
                auditService.log("PASSWORD_RESET_CONFIRM", "FAILED", null, null, "Unknown password reset token");
                return new InvalidPasswordResetTokenException();
            });

        if (resetToken.isUsed()) {
            auditService.log("PASSWORD_RESET_CONFIRM", "FAILED", resetToken.getOwner(), resetToken.getOwner().getUsername(), "Used password reset token");
            throw new PasswordResetTokenAlreadyUsedException();
        }

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            auditService.log("PASSWORD_RESET_CONFIRM", "FAILED", resetToken.getOwner(), resetToken.getOwner().getUsername(), "Expired password reset token");
            throw new PasswordResetTokenExpiredException();
        }

        User user = resetToken.getOwner();

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        resetToken.setUsedAt(LocalDateTime.now());
        refreshTokenService.revokeAllForUser(user);

        auditService.log("PASSWORD_RESET_CONFIRM", "SUCCESS", user, user.getUsername(), "Password reset completed and refresh tokens revoked");

        return new MessageResponseDto("Password has been reset successfully.");
    }

    public void deleteExpiredTokens() {
        passwordResetTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }

    private MessageResponseDto genericResponse() {
        return new MessageResponseDto(
            "If an account with this email exists, password reset instructions have been sent."
        );
    }

    private String maskEmail(String email) {

        int atIndex = email.indexOf('@');

        if (atIndex <= 1) {
            return "***";
        }

        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}
