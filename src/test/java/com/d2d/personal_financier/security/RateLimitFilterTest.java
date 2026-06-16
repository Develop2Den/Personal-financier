package com.d2d.personal_financier.security;

import com.d2d.personal_financier.config.security.utils.RateLimitFilter;
import com.d2d.personal_financier.config.security.utils.RateLimitService;
import com.d2d.personal_financier.config.security.utils.SecurityErrorResponseWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RateLimitFilterTest {

    @Test
    void shouldReturnJsonAndRetryAfterWhenLimitExceeded() throws Exception {
        RateLimitService rateLimitService = mock(RateLimitService.class);
        SecurityErrorResponseWriter errorResponseWriter =
            new SecurityErrorResponseWriter(new ObjectMapper().findAndRegisterModules());
        RateLimitFilter rateLimitFilter =
            new RateLimitFilter(rateLimitService, errorResponseWriter);

        Bucket bucket = Bucket.builder()
            .addLimit(Bandwidth.builder()
                .capacity(1)
                .refillGreedy(1, Duration.ofMinutes(1))
                .build())
            .build();
        bucket.tryConsume(1);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/auth/register");
        request.setServletPath("/auth/register");
        request.setRemoteAddr("127.0.0.1");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        when(rateLimitService.resolveBucket("127.0.0.1", "/auth/register")).thenReturn(bucket);

        rateLimitFilter.doFilter(request, response, filterChain);

        assertEquals(429, response.getStatus());
        assertEquals("application/json;charset=UTF-8", response.getContentType());
        assertNotNull(response.getHeader("Retry-After"));
        assertTrue(Integer.parseInt(response.getHeader("Retry-After")) >= 1);
        assertTrue(response.getContentAsString().contains("\"status\":429"));
        assertTrue(response.getContentAsString().contains("\"message\":\"Too many requests\""));
        assertTrue(response.getContentAsString().contains("\"path\":\"/auth/register\""));
        verifyNoInteractions(filterChain);
    }
}
