package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.dto.analytics.CategoryBreakdownDto;
import com.d2d.personal_financier.dto.analytics.DashboardDto;
import com.d2d.personal_financier.dto.analytics.MonthlyCashflowDto;
import com.d2d.personal_financier.dto.analytics.MonthlyExpenseDto;
import com.d2d.personal_financier.entity.Account;
import com.d2d.personal_financier.entity.Category;
import com.d2d.personal_financier.entity.Goal;
import com.d2d.personal_financier.entity.Transaction;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.entity.enums.TransactionType;
import com.d2d.personal_financier.repository.AccountRepository;
import com.d2d.personal_financier.repository.GoalRepository;
import com.d2d.personal_financier.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void getMonthlyExpensesShouldSumOnlyExpenseTransactions() {
        User user = User.builder().id(1L).build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findByOwnerId(1L)).thenReturn(buildTransactions(user));

        List<MonthlyExpenseDto> result = analyticsService.getMonthlyExpenses();

        assertEquals(1, result.size());
        assertEquals("2026-04", result.getFirst().month());
        assertEquals(new BigDecimal("50.00"), result.getFirst().total());
    }

    @Test
    void getDashboardShouldUseExpensesBalanceAndTopCategoryWithoutFailing() {
        User user = User.builder().id(1L).build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findByOwnerId(1L)).thenReturn(buildTransactions(user));
        when(accountRepository.findByOwnerId(1L)).thenReturn(List.of(
            new Account(1L, "Main", "USD", new BigDecimal("150.00"), null, user, List.of())
        ));
        when(goalRepository.findByOwnerId(1L)).thenReturn(List.of(new Goal(), new Goal()));

        DashboardDto result = analyticsService.getDashboard("2026-04");

        assertEquals(new BigDecimal("150.00"), result.totalBalance());
        assertEquals(new BigDecimal("100.00"), result.monthlyIncome());
        assertEquals(new BigDecimal("50.00"), result.monthlyExpenses());
        assertEquals(new BigDecimal("50.00"), result.netCashflow());
        assertEquals("Food", result.topExpenseCategory());
        assertEquals(2L, result.activeGoals());
        assertEquals(3L, result.monthlyTransactionCount());
    }

    @Test
    void getTopCategoriesShouldGroupExpensesAndIgnoreIncome() {
        User user = User.builder().id(1L).build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findByOwnerId(1L)).thenReturn(buildTransactions(user));

        List<CategoryBreakdownDto> result = analyticsService.getTopCategories("2026-04");

        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).category());
        assertEquals(new BigDecimal("30.00"), result.get(0).amount());
        assertEquals(new BigDecimal("60.00"), result.get(0).percentage());
        assertEquals(1L, result.get(0).transactionCount());
        assertEquals("Transport", result.get(1).category());
        assertEquals(new BigDecimal("20.00"), result.get(1).amount());
        assertEquals(new BigDecimal("40.00"), result.get(1).percentage());
    }

    @Test
    void getMonthlyCashflowShouldReturnIncomeExpensesAndNetByMonth() {
        User user = User.builder().id(1L).build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findByOwnerId(1L)).thenReturn(buildTransactions(user));

        List<MonthlyCashflowDto> result = analyticsService.getMonthlyCashflow();

        assertEquals(1, result.size());
        assertEquals("2026-04", result.getFirst().month());
        assertEquals(new BigDecimal("100.00"), result.getFirst().income());
        assertEquals(new BigDecimal("50.00"), result.getFirst().expenses());
        assertEquals(new BigDecimal("50.00"), result.getFirst().net());
    }

    @Test
    void getDashboardShouldRejectInvalidMonthFormat() {
        User user = User.builder().id(1L).build();

        when(securityUtils.getCurrentUser()).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> analyticsService.getDashboard("04-2026"));
    }

    private List<Transaction> buildTransactions(User user) {
        Category food = new Category();
        food.setName("Food");

        Category transport = new Category();
        transport.setName("Transport");

        return List.of(
            Transaction.builder()
                .owner(user)
                .type(TransactionType.EXPENSE)
                .amount(new BigDecimal("30.00"))
                .date(LocalDateTime.of(2026, 4, 10, 12, 0))
                .category(food)
                .build(),
            Transaction.builder()
                .owner(user)
                .type(TransactionType.EXPENSE)
                .amount(new BigDecimal("20.00"))
                .date(LocalDateTime.of(2026, 4, 12, 12, 0))
                .category(transport)
                .build(),
            Transaction.builder()
                .owner(user)
                .type(TransactionType.INCOME)
                .amount(new BigDecimal("100.00"))
                .date(LocalDateTime.of(2026, 4, 15, 12, 0))
                .category(null)
                .build()
        );
    }
}
