package com.d2d.personal_financier.config.security.utils;

import com.d2d.personal_financier.dto.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException {

        String ip = request.getRemoteAddr();

        Bucket bucket = rateLimitService.resolveBucket(ip, request.getServletPath());
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {

            filterChain.doFilter(request, response);

        } else {
            long retryAfterSeconds = Math.max(
                1,
                TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()) +
                    (probe.getNanosToWaitForRefill() % 1_000_000_000L == 0 ? 0 : 1)
            );

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));

            ErrorResponse error = new ErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Too many requests",
                request.getRequestURI(),
                LocalDateTime.now()
            );

            objectMapper.writeValue(response.getWriter(), error);

        }
    }
}
