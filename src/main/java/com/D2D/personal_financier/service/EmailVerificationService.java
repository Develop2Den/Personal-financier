package com.D2D.personal_financier.service;

import com.D2D.personal_financier.dto.message.MessageResponseDto;
import com.D2D.personal_financier.entity.EmailVerificationToken;
import com.D2D.personal_financier.entity.User;
import com.D2D.personal_financier.exception.InvalidVerificationTokenException;
import com.D2D.personal_financier.exception.VerificationTokenAlreadyUsedException;
import com.D2D.personal_financier.exception.VerificationTokenExpiredException;
import com.D2D.personal_financier.repository.EmailVerificationTokenRepository;
import com.D2D.personal_financier.repository.UserRepository;
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
            return new MessageResponseDto(
                "If an account with this email exists and is not verified, a new verification email has been sent."
            );
        }

        if (Boolean.TRUE.equals(user.getVerified())) {
            return new MessageResponseDto(
                "Email is already verified. You can log in."
            );
        }

        String emailToken = generateToken(user);
        emailService.sendVerificationEmail(user.getEmail(), emailToken);

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

        tokenRepository.delete(verificationToken);
    }
}


