package com.D2D.personal_financier.service;

import com.D2D.personal_financier.entity.EmailVerificationToken;
import com.D2D.personal_financier.entity.User;
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

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public String generateToken(User owner) {

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

    public void verifyToken(String token) {

        EmailVerificationToken verificationToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (!verificationToken.isLive()) {
            throw new RuntimeException("Token already used");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(verificationToken);
            throw new RuntimeException("Token expired");
        }

        User user = verificationToken.getOwner();

        user.setVerified(true);

        userRepository.save(user);

        tokenRepository.delete(verificationToken);
    }
}


