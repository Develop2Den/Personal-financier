package com.d2d.personal_financier.journey;

import com.d2d.personal_financier.config.security.SecurityConfig;
import com.d2d.personal_financier.config.security.jwt.JwtAuthFilter;
import com.d2d.personal_financier.config.security.jwt.JwtProvider;
import com.d2d.personal_financier.config.security.utils.JwtBlacklistService;
import com.d2d.personal_financier.config.security.utils.RateLimitFilter;
import com.d2d.personal_financier.controller.AccountController;
import com.d2d.personal_financier.controller.AnalyticsController;
import com.d2d.personal_financier.controller.AuthController;
import com.d2d.personal_financier.controller.BudgetController;
import com.d2d.personal_financier.controller.CategoryController;
import com.d2d.personal_financier.controller.GoalController;
import com.d2d.personal_financier.controller.TransactionController;
import com.d2d.personal_financier.dto.account_dto.AccountResponseDto;
import com.d2d.personal_financier.dto.analytics.CategoryBreakdownDto;
import com.d2d.personal_financier.dto.analytics.DashboardDto;
import com.d2d.personal_financier.dto.analytics.MonthlyCashflowDto;
import com.d2d.personal_financier.dto.auth_dto.AuthResponseDto;
import com.d2d.personal_financier.dto.budget_dto.BudgetResponseDto;
import com.d2d.personal_financier.dto.category_dto.CategoryResponseDto;
import com.d2d.personal_financier.dto.goal_dto.GoalResponseDto;
import com.d2d.personal_financier.dto.message.MessageResponseDto;
import com.d2d.personal_financier.dto.transaction_dto.TransactionResponseDto;
import com.d2d.personal_financier.dto.transaction_dto.TransferResponseDto;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.entity.enums.AccountType;
import com.d2d.personal_financier.entity.enums.BudgetPeriod;
import com.d2d.personal_financier.entity.enums.TransactionType;
import com.d2d.personal_financier.exception.GlobalExceptionHandler;
import com.d2d.personal_financier.exception.InvalidRefreshTokenException;
import com.d2d.personal_financier.repository.UserRepository;
import com.d2d.personal_financier.service.AccountService;
import com.d2d.personal_financier.service.AnalyticsService;
import com.d2d.personal_financier.service.AuditService;
import com.d2d.personal_financier.service.BudgetService;
import com.d2d.personal_financier.service.CategoryService;
import com.d2d.personal_financier.service.EmailVerificationService;
import com.d2d.personal_financier.service.GoalService;
import com.d2d.personal_financier.service.PasswordResetService;
import com.d2d.personal_financier.service.RefreshTokenService;
import com.d2d.personal_financier.service.TransactionService;
import com.d2d.personal_financier.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
    AuthController.class,
    AccountController.class,
    CategoryController.class,
    TransactionController.class,
    BudgetController.class,
    GoalController.class,
    AnalyticsController.class
})
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class UserJourneyWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

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
    private AccountService accountService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private BudgetService budgetService;

    @MockBean
    private GoalService goalService;

    @MockBean
    private AnalyticsService analyticsService;

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
    void userJourneyShouldPassThroughCoreFlow() throws Exception {
        String username = "denisdev";
        String email = "denis@example.com";
        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        String resetToken = "reset-token";
        LocalDateTime now = LocalDateTime.of(2026, 4, 23, 10, 0);

        User userEntity = User.builder()
            .id(1L)
            .username(username)
            .email(email)
            .verified(true)
            .build();
        Claims accessClaims = Jwts.claims().subject(username).build();

        when(userService.register(any())).thenReturn(
            new MessageResponseDto("Registration successful. Please check your email and verify it before logging in.")
        );
        doNothing().when(emailVerificationService).verifyToken("verify-token");
        when(userService.login(username, "MyPass123!")).thenReturn(
            new AuthResponseDto(accessToken, refreshToken)
        );

        when(accountService.createAccount(any())).thenReturn(
            new AccountResponseDto(10L, "Main Card", "USD", new BigDecimal("1000.00"), AccountType.CARD)
        );

        when(categoryService.createCategory(any())).thenReturn(
            new CategoryResponseDto(20L, "Food", TransactionType.EXPENSE),
            new CategoryResponseDto(21L, "Salary", TransactionType.INCOME)
        );

        when(transactionService.createTransaction(any())).thenReturn(
            new TransactionResponseDto(30L, new BigDecimal("50.00"), TransactionType.EXPENSE, "Groceries", now, 10L, 20L),
            new TransactionResponseDto(31L, new BigDecimal("2500.00"), TransactionType.INCOME, "Monthly salary", now, 10L, 21L)
        );
        when(transactionService.transferBetweenAccounts(any())).thenReturn(
            new TransferResponseDto(
                "transfer-ref",
                new BigDecimal("100.00"),
                "USD",
                "Card to cash",
                now,
                10L,
                11L,
                32L,
                33L,
                new BigDecimal("900.00"),
                new BigDecimal("100.00")
            )
        );

        when(budgetService.createBudget(any())).thenReturn(
            new BudgetResponseDto(40L, 20L, new BigDecimal("600.00"), LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30), BudgetPeriod.MONTHLY)
        );

        when(goalService.createGoal(any())).thenReturn(
            new GoalResponseDto(50L, "Emergency fund", new BigDecimal("5000.00"), new BigDecimal("0.00"), 0, LocalDate.of(2026, 12, 31))
        );

        when(analyticsService.getMonthlyCashflow()).thenReturn(List.of(
            new MonthlyCashflowDto("2026-04", new BigDecimal("2500.00"), new BigDecimal("50.00"), new BigDecimal("2450.00"))
        ));
        when(analyticsService.getTopCategories("2026-04")).thenReturn(List.of(
            new CategoryBreakdownDto("Food", new BigDecimal("50.00"), new BigDecimal("100.00"), 1L)
        ));
        when(analyticsService.getDashboard("2026-04")).thenReturn(
            new DashboardDto(
                new BigDecimal("3450.00"),
                new BigDecimal("2500.00"),
                new BigDecimal("50.00"),
                new BigDecimal("2450.00"),
                "Food",
                1L,
                2L
            )
        );

        when(jwtProvider.getValidatedClaims(accessToken)).thenReturn(Optional.of(accessClaims));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
        doNothing().when(refreshTokenService).revoke(refreshToken);
        when(refreshTokenService.refresh(refreshToken)).thenThrow(new InvalidRefreshTokenException());

        when(passwordResetService.requestReset(email)).thenReturn(
            new MessageResponseDto("If an account with this email exists, password reset instructions have been sent.")
        );
        when(passwordResetService.confirmReset(any())).thenReturn(
            new MessageResponseDto("Password has been reset successfully.")
        );

        mockMvc.perform(post("/auth/register")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "username": "denisdev",
                      "email": "denis@example.com",
                      "password": "MyPass123!"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("Registration successful. Please check your email and verify it before logging in."));

        mockMvc.perform(get("/auth/verify-email").param("token", "verify-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Email verified successfully!"));

        mockMvc.perform(post("/auth/login")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "username": "denisdev",
                      "password": "MyPass123!"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(accessToken))
            .andExpect(jsonPath("$.refreshToken").value(refreshToken));

        mockMvc.perform(post("/api/accounts")
                .with(user(username))
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "name": "Main Card",
                      "currency": "USD",
                      "balance": 1000.00,
                      "type": "CARD"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10L));

        mockMvc.perform(post("/api/categories")
                .with(user(username))
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "name": "Food",
                      "type": "EXPENSE"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(20L));

        mockMvc.perform(post("/api/categories")
                .with(user(username))
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "name": "Salary",
                      "type": "INCOME"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(21L));

        mockMvc.perform(post("/api/transactions")
                .with(user(username))
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "amount": 50.00,
                      "type": "EXPENSE",
                      "description": "Groceries",
                      "date": "2026-04-23T10:00:00",
                      "accountId": 10,
                      "categoryId": 20
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(30L));

        mockMvc.perform(post("/api/transactions")
                .with(user(username))
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "amount": 2500.00,
                      "type": "INCOME",
                      "description": "Monthly salary",
                      "date": "2026-04-23T10:00:00",
                      "accountId": 10,
                      "categoryId": 21
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(31L));

        mockMvc.perform(post("/api/budgets")
                .with(user(username))
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "categoryId": 20,
                      "limitAmount": 600.00,
                      "startDate": "2026-04-01",
                      "endDate": "2026-04-30",
                      "period": "MONTHLY"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(40L));

        mockMvc.perform(post("/api/goals")
                .with(user(username))
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "name": "Emergency fund",
                      "targetAmount": 5000.00,
                      "deadline": "2026-12-31"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(50L));

        mockMvc.perform(get("/api/analytics/monthly-cashflow")
                .with(user(username)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].month").value("2026-04"))
            .andExpect(jsonPath("$[0].income").value(2500.00))
            .andExpect(jsonPath("$[0].expenses").value(50.00));

        mockMvc.perform(get("/api/analytics/top-categories")
                .with(user(username))
                .param("month", "2026-04"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].category").value("Food"))
            .andExpect(jsonPath("$[0].amount").value(50.00));

        mockMvc.perform(get("/api/analytics/dashboard")
                .with(user(username))
                .param("month", "2026-04"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalBalance").value(3450.00))
            .andExpect(jsonPath("$.netCashflow").value(2450.00));

        mockMvc.perform(post("/auth/logout")
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .content("""
                    {
                      "refreshToken": "refresh-token"
                    }
                    """))
            .andExpect(status().isNoContent());

        mockMvc.perform(post("/auth/refresh")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "refreshToken": "refresh-token"
                    }
                    """))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Refresh token is invalid or has been revoked"));

        mockMvc.perform(post("/auth/password-reset")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "email": "denis@example.com"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("If an account with this email exists, password reset instructions have been sent."));

        mockMvc.perform(post("/auth/password-reset/confirm")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "token": "%s",
                      "newPassword": "NewPass123!"
                    }
                    """.formatted(resetToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Password has been reset successfully."));
    }

    @Test
    void pagedListEndpointsShouldReturnSpringPagePayload() throws Exception {
        String username = "denisdev";

        when(accountService.getAllAccounts(any())).thenReturn(
            new PageImpl<>(
                List.of(new AccountResponseDto(10L, "Main Card", "USD", new BigDecimal("1000.00"), AccountType.CARD)),
                PageRequest.of(0, 20),
                1
            )
        );

        when(categoryService.getAllCategories(any())).thenReturn(
            new PageImpl<>(
                List.of(new CategoryResponseDto(20L, "Food", TransactionType.EXPENSE)),
                PageRequest.of(0, 20),
                1
            )
        );

        when(transactionService.getAllTransactions(any())).thenReturn(
            new PageImpl<>(
                List.of(new TransactionResponseDto(
                    30L,
                    new BigDecimal("50.00"),
                    TransactionType.EXPENSE,
                    "Groceries",
                    LocalDateTime.of(2026, 4, 23, 10, 0),
                    10L,
                    20L
                )),
                PageRequest.of(0, 20),
                1
            )
        );

        mockMvc.perform(get("/api/accounts")
                .with(user(username))
                .param("page", "0")
                .param("size", "20")
                .param("sort", "id,asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(10L))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(20));

        mockMvc.perform(get("/api/categories")
                .with(user(username))
                .param("page", "0")
                .param("size", "20")
                .param("sort", "id,asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(20L))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(20));

        mockMvc.perform(get("/api/transactions")
                .with(user(username))
                .param("page", "0")
                .param("size", "20")
                .param("sort", "date,asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(30L))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    void transferEndpointShouldReturnTransferPayload() throws Exception {
        when(transactionService.transferBetweenAccounts(any())).thenReturn(
            new TransferResponseDto(
                "transfer-ref",
                new BigDecimal("150.00"),
                "USD",
                "Card to cash",
                LocalDateTime.of(2026, 4, 27, 11, 0),
                10L,
                11L,
                101L,
                102L,
                new BigDecimal("850.00"),
                new BigDecimal("250.00")
            )
        );

        mockMvc.perform(post("/api/transactions/transfer")
                .with(user("denisdev"))
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "fromAccountId": 10,
                      "toAccountId": 11,
                      "amount": 150.00,
                      "description": "Card to cash",
                      "date": "2026-04-27T11:00:00"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.transferReference").value("transfer-ref"))
            .andExpect(jsonPath("$.fromAccountId").value(10L))
            .andExpect(jsonPath("$.toAccountId").value(11L))
            .andExpect(jsonPath("$.outgoingTransactionId").value(101L))
            .andExpect(jsonPath("$.incomingTransactionId").value(102L));
    }
}
