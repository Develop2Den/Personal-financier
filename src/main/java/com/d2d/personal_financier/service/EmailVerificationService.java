package com.d2d.personal_financier.service;

import com.d2d.personal_financier.dto.message.MessageResponseDto;
import com.d2d.personal_financier.entity.EmailVerificationToken;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.exception.InvalidVerificationTokenException;
import com.d2d.personal_financier.exception.VerificationTokenAlreadyUsedException;
import com.d2d.personal_financier.exception.VerificationTokenExpiredException;
import com.d2d.personal_financier.repository.EmailVerificationTokenRepository;
import com.d2d.personal_financier.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailVerificationService {

    private final EmailService emailService;
    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public String generateToken(User owner) {

        tokenRepository.deleteByOwner(owner);

        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken =
                EmailVerificationToken.builder()
                        .token(token)
                        .owner(owner)
                        .isLive(true)
                        .expiryDate(LocalDateTime.now().plusHours(24))
                        .build();

        tokenRepository.save(verificationToken);

        return token;
    }

    public MessageResponseDto resendVerificationEmail(String email) {

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            auditService.log("VERIFICATION_RESEND", "IGNORED", null, email, "Verification resend requested for unknown email");
            return new MessageResponseDto(
                "If an account with this email exists and is not verified, a new verification email has been sent."
            );
        }

        if (Boolean.TRUE.equals(user.getVerified())) {
            auditService.log("VERIFICATION_RESEND", "IGNORED", user, user.getUsername(), "Verification resend requested for verified account");
            return new MessageResponseDto(
                "Email is already verified. You can log in."
            );
        }

        String emailToken = generateToken(user);
        emailService.sendVerificationEmail(user.getEmail(), emailToken);
        auditService.log("VERIFICATION_RESEND", "SUCCESS", user, user.getUsername(), "Verification email resent");

        return new MessageResponseDto(
            "A new verification email has been sent. Please check your inbox."
        );
    }

    public void verifyToken(String token) {

        EmailVerificationToken verificationToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(InvalidVerificationTokenException::new);

        if (!verificationToken.isLive()) {
            throw new VerificationTokenAlreadyUsedException();
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(verificationToken);
            throw new VerificationTokenExpiredException();
        }

        User user = verificationToken.getOwner();

        user.setVerified(true);

        userRepository.save(user);
        auditService.log("EMAIL_VERIFICATION", "SUCCESS", user, user.getUsername(), "Email verified");

        tokenRepository.delete(verificationToken);
    }
}

