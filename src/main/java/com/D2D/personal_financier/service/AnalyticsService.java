package com.D2D.personal_financier.service;

import com.D2D.personal_financier.config.security.utils.SecurityUtils;
import com.D2D.personal_financier.dto.analytics.CategoryReportDto;
import com.D2D.personal_financier.dto.analytics.DashboardDto;
import com.D2D.personal_financier.dto.analytics.MonthlyExpenseDto;
import com.D2D.personal_financier.entity.Account;
import com.D2D.personal_financier.entity.User;
import com.D2D.personal_financier.repository.AccountRepository;
import com.D2D.personal_financier.repository.AnalyticsRepository;
import com.D2D.personal_financier.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final AccountRepository accountRepository;
    private final GoalRepository goalRepository;
    private final SecurityUtils securityUtils;

    public List<MonthlyExpenseDto> getMonthlyExpenses() {

        User user = securityUtils.getCurrentUser();

        return analyticsRepository.getMonthlyExpenses(user.getId());
    }

    public List<CategoryReportDto> getTopCategories() {

        User user = securityUtils.getCurrentUser();

        return analyticsRepository.getTopCategories(user.getId());
    }

    public DashboardDto getDashboard() {

        User user = securityUtils.getCurrentUser();

        BigDecimal totalBalance =
                accountRepository.findByOwnerId(user.getId())
                        .stream()
                        .map(Account::getBalance)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthlyExpenses =
                analyticsRepository.getMonthlyExpenses(user.getId())
                        .stream()
                        .map(MonthlyExpenseDto::total)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        String topCategory =
                analyticsRepository.getTopCategories(user.getId())
                        .stream()
                        .findFirst()
                        .map(CategoryReportDto::category)
                        .orElse("None");

        Long activeGoals =
                goalRepository.findByOwnerId(user.getId())
                        .stream()
                        .count();

        return new DashboardDto(
                totalBalance,
                monthlyExpenses,
                topCategory,
                activeGoals
        );
    }

}
