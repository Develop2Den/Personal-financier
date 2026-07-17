package com.d2d.personal_financier.security;

import com.d2d.personal_financier.config.security.ApiAccessDeniedHandler;
import com.d2d.personal_financier.config.security.ApiAuthenticationEntryPoint;
import com.d2d.personal_financier.config.security.SecurityConfig;
import com.d2d.personal_financier.config.security.jwt.JwtAuthFilter;
import com.d2d.personal_financier.config.security.jwt.JwtProvider;
import com.d2d.personal_financier.config.security.utils.JwtBlacklistService;
import com.d2d.personal_financier.config.security.utils.RateLimitFilter;
import com.d2d.personal_financier.config.security.utils.SecurityErrorResponseWriter;
import com.d2d.personal_financier.controller.AnalyticsController;
import com.d2d.personal_financier.controller.AuthController;
import com.d2d.personal_financier.dto.analytics.DashboardDto;
import com.d2d.personal_financier.exception.GlobalExceptionHandler;
import com.d2d.personal_financier.repository.UserRepository;
import com.d2d.personal_financier.service.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AuthController.class, AnalyticsController.class})
@Import({
    SecurityConfig.class,
    GlobalExceptionHandler.class,
    ApiAuthenticationEntryPoint.class,
    ApiAccessDeniedHandler.class,
    SecurityErrorResponseWriter.class
})
class SecurityConfigurationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyticsService analyticsService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private EmailVerificationService emailVerificationService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private PasswordResetService passwordResetService;

    @MockitoBean
    private AuditService auditService;

    @MockitoBean
    private JwtBlacklistService jwtBlacklistService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private RateLimitFilter rateLimitFilter;

    @BeforeEach
    void setUpFilters() throws Exception {
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());

        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(rateLimitFilter).doFilter(any(), any(), any());
    }

    @Test
    void protectedEndpointShouldRejectAnonymousRequests() throws Exception {
        mockMvc.perform(get("/api/analytics/dashboard"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.message").value("Authentication is required"))
            .andExpect(jsonPath("$.path").value("/api/analytics/dashboard"));
    }

    @Test
    @WithMockUser(username = "denisdev")
    void protectedEndpointShouldAllowAuthenticatedRequests() throws Exception {
        when(analyticsService.getDashboard(null)).thenReturn(
            new DashboardDto(
                new BigDecimal("100.00"),
                new BigDecimal("50.00"),
                new BigDecimal("20.00"),
                new BigDecimal("30.00"),
                "Food",
                2L,
                3L
            )
        );

        mockMvc.perform(get("/api/analytics/dashboard"))
            .andExpect(status().isOk());
    }

    @Test
    void publicAuthEndpointShouldExposeSecurityHeaders() throws Exception {
        doNothing().when(emailVerificationService).verifyToken(any());

        mockMvc.perform(get("/auth/verify-email").param("token", "token-123"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Security-Policy", "default-src 'self'; frame-ancestors 'none'; base-uri 'self'; form-action 'self'"))
            .andExpect(header().string("X-Content-Type-Options", "nosniff"))
            .andExpect(header().string("X-Frame-Options", "DENY"))
            .andExpect(header().string("Referrer-Policy", "strict-origin-when-cross-origin"))
            .andExpect(header().exists("Permissions-Policy"));
    }

    @Test
    void invalidMonthShouldReturnBadRequest() throws Exception {
        when(analyticsService.getDashboard("04-2026"))
            .thenThrow(new IllegalArgumentException("Invalid month format. Expected YYYY-MM"));

        mockMvc.perform(get("/api/analytics/dashboard").param("month", "04-2026").with(user("denisdev")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void loginEndpointShouldRemainPublic() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "username": "",
                      "password": ""
                    }
                    """))
            .andExpect(status().isBadRequest());
    }
}
