package com.D2D.personal_financier.service;

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

        String subject = "Подтверждение регистрации";

        String verificationUrl =
                baseUrl + "/auth/verify-email?token=" + token;

        String message =
                "Привет!\n\n" +
                        "Пожалуйста, подтвердите регистрацию:\n" +
                        verificationUrl + "\n\n" +
                        "Ссылка действует 24 часа.";

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }
}

