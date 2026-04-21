package com.d2d.personal_financier.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public void sendVerificationEmail(String to, String token) {

        String verificationUrl =
            baseUrl + "/auth/verify-email?token=" + token;

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom("FinalProjectLinkedin@gmail.com");
        mailMessage.setTo(to);
        mailMessage.setSubject("Registration Confirmation");
        mailMessage.setText(
            "Hello,\n\n" +
                "Thank you for registering.\n\n" +
                "Please confirm your email address by clicking the link below:\n" +
                verificationUrl + "\n\n" +
                "This verification link will expire in 24 hours.\n\n" +
                "If you did not create this account, you can safely ignore this email."
        );

        mailSender.send(mailMessage);
    }
}

