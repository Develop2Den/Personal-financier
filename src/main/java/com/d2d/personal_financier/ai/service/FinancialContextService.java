package com.d2d.personal_financier.ai.service;

import com.d2d.personal_financier.ai.dto.*;
import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.dto.analytics.DashboardDto;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.repository.AccountRepository;
import com.d2d.personal_financier.repository.CategoryRepository;
import com.d2d.personal_financier.repository.GoalRepository;
import com.d2d.personal_financier.repository.TransactionRepository;
import com.d2d.personal_financier.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialContextService {

    private final AnalyticsService analyticsService;
    private final AccountRepository accountRepository;
    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final FinancialMetricsService financialMetricsService;
    private final CategoryRepository categoryRepository;
    private final SecurityUtils securityUtils;

    public FinancialContextDto buildContext() {

        DashboardDto dashboard = analyticsService.getDashboard(null);
        FinancialMetricsDto metrics = financialMetricsService.buildMetrics();
        User user = securityUtils.getCurrentUser();

        List<AccountSummaryDto> accounts =
            accountRepository.findByOwnerId(user.getId())
                .stream()
                .map(account -> new AccountSummaryDto(
                    account.getName(),
                    account.getType(),
                    account.getBalance(),
                    account.getCurrency()
                ))
                .toList();

        List<GoalSummaryDto> goals =
            goalRepository.findByOwnerId(user.getId())
                .stream()
                .map(goal -> new GoalSummaryDto(
                    goal.getName(),
                    goal.getTargetAmount(),
                    goal.getCurrentAmount(),
                    goal.getDeadline(),
                    goal.getStatus()
                ))
                .toList();

        List<TransactionSummaryDto> transactions =
            transactionRepository.findByOwnerId(
                    user.getId(),
                    PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "date"))
                )
                .getContent()
                .stream()
                .map(transaction -> new TransactionSummaryDto(
                    transaction.getDate(),
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getAccount().getCurrency(),
                    transaction.getAccount().getName(),
                    transaction.getCategory() != null
                        ? transaction.getCategory().getName()
                        : null,
                    transaction.getDescription(),
                    transaction.getTransferDirection()
                ))
                .toList();

        List<CategorySummaryDto> categories =
            categoryRepository.findByOwnerIdAndActiveTrue(user.getId())
                .stream()
                .map(category -> new CategorySummaryDto(
                    category.getName(),
                    category.getType(),
                    category.getActive()
                ))
                .toList();

        return new FinancialContextDto(
            dashboard,
            metrics,
            accounts,
            goals,
            transactions,
            categories
        );
    }
}
