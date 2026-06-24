package com.d2d.personal_financier.ai.service;

import com.d2d.personal_financier.dto.analytics.DashboardDto;
import com.d2d.personal_financier.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialContextService {

    private final AnalyticsService analyticsService;

    public String buildContext() {

        DashboardDto dashboard = analyticsService.getDashboard(null);

        return """
            Financial Summary:

            Total Balance: %s UAH
            Monthly Income: %s UAH
            Monthly Expenses: %s UAH
            Net Cashflow: %s UAH

            Top Expense Category: %s
            Active Goals: %d
            Monthly Transaction Count: %d
            """
            .formatted(
                dashboard.totalBalance(),
                dashboard.monthlyIncome(),
                dashboard.monthlyExpenses(),
                dashboard.netCashflow(),
                dashboard.topExpenseCategory(),
                dashboard.activeGoals(),
                dashboard.monthlyTransactionCount()
            );
    }
}
