package com.d2d.personal_financier.config.security.utils;

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
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final SecurityErrorResponseWriter errorResponseWriter;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException {

        String path = request.getServletPath();

        if (path.startsWith("/swagger-ui")
            || path.startsWith("/v3/api-docs")) {

            filterChain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();

        Bucket bucket = rateLimitService.resolveBucket(ip, path);
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
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));

            errorResponseWriter.write(
                request,
                response,
                HttpStatus.TOO_MANY_REQUESTS,
                "Too many requests"
            );
        }
    }
}
