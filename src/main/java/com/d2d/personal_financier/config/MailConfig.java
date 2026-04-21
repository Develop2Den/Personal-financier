package com.d2d.personal_financier.config;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
@Data
public class MailConfig {

    private String host;
    private int port;
    private String username;
    private String password;
    private Properties properties = new Properties();

    public void setHost(String host) { this.host = host; }
    public void setPort(int port) { this.port = port; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setProperties(Properties properties) { this.properties = properties; }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setJavaMailProperties(properties);
        return mailSender;
    }
}


