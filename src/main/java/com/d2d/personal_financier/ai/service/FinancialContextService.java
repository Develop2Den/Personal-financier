package com.d2d.personal_financier.ai.service;

import com.d2d.personal_financier.ai.dto.AccountSummaryDto;
import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import com.d2d.personal_financier.ai.dto.GoalSummaryDto;
import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.dto.analytics.DashboardDto;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.repository.AccountRepository;
import com.d2d.personal_financier.repository.GoalRepository;
import com.d2d.personal_financier.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialContextService {

    private final AnalyticsService analyticsService;
    private final AccountRepository accountRepository;
    private final GoalRepository goalRepository;
    private final SecurityUtils securityUtils;

    public FinancialContextDto buildContext() {

        User user = securityUtils.getCurrentUser();
        DashboardDto dashboard = analyticsService.getDashboard(null);

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

        return new FinancialContextDto(
            dashboard,
            accounts,
            goals
        );
    }
}
