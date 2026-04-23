package com.d2d.personal_financier.security;

import com.d2d.personal_financier.config.security.SecurityConfig;
import com.d2d.personal_financier.config.security.jwt.JwtAuthFilter;
import com.d2d.personal_financier.config.security.jwt.JwtProvider;
import com.d2d.personal_financier.config.security.utils.JwtBlacklistService;
import com.d2d.personal_financier.config.security.utils.RateLimitFilter;
import com.d2d.personal_financier.controller.AnalyticsController;
import com.d2d.personal_financier.controller.AuthController;
import com.d2d.personal_financier.dto.analytics.DashboardDto;
import com.d2d.personal_financier.exception.GlobalExceptionHandler;
import com.d2d.personal_financier.repository.UserRepository;
import com.d2d.personal_financier.service.AnalyticsService;
import com.d2d.personal_financier.service.AuditService;
import com.d2d.personal_financier.service.EmailVerificationService;
import com.d2d.personal_financier.service.PasswordResetService;
import com.d2d.personal_financier.service.RefreshTokenService;
import com.d2d.personal_financier.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, AnalyticsController.class})
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class SecurityConfigurationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @MockBean
    private UserService userService;

    @MockBean
    private EmailVerificationService emailVerificationService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private PasswordResetService passwordResetService;

    @MockBean
    private AuditService auditService;

    @MockBean
    private JwtBlacklistService jwtBlacklistService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
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
            .andExpect(status().isForbidden());
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
