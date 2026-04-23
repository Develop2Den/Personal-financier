package com.d2d.personal_financier.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    private final Cors cors = new Cors();
    private final Headers headers = new Headers();

    @Getter
    @Setter
    public static class Cors {

        private List<String> allowedOrigins = new ArrayList<>();
        private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");
        private List<String> allowedHeaders = List.of("Authorization", "Content-Type");
        private List<String> exposedHeaders = List.of("Authorization");
        private boolean allowCredentials = true;
        private long maxAge = 3600;
    }

    @Getter
    @Setter
    public static class Headers {

        private String contentSecurityPolicy = "default-src 'self'";
        private String referrerPolicy = "strict-origin-when-cross-origin";
        private String permissionsPolicy = "camera=(), microphone=(), geolocation=()";
    }
}
