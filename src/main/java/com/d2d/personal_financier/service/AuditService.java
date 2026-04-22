package com.d2d.personal_financier.service;

import com.d2d.personal_financier.entity.AuditLog;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(String eventType, String status, User user, String principal, String details) {

        HttpServletRequest request = getCurrentRequest();

        auditLogRepository.save(
            AuditLog.builder()
                .eventType(eventType)
                .status(status)
                .user(user)
                .principal(truncate(principal, 100))
                .ipAddress(request != null ? truncate(resolveClientIp(request), 64) : null)
                .userAgent(request != null ? truncate(request.getHeader("User-Agent"), 255) : null)
                .details(truncate(details, 500))
                .build()
        );
    }

    private HttpServletRequest getCurrentRequest() {

        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            return servletAttributes.getRequest();
        }

        return null;
    }

    private String resolveClientIp(HttpServletRequest request) {

        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    private String truncate(String value, int maxLength) {

        if (value == null || value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength);
    }
}
