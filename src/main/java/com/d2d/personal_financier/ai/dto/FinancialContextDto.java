package com.d2d.personal_financier.ai.dto;

import com.d2d.personal_financier.dto.analytics.DashboardDto;

import java.util.List;

public record FinancialContextDto(

    DashboardDto dashboard,

    FinancialMetricsDto metrics,

    List<AccountSummaryDto> accounts,

    List<GoalSummaryDto> goals,

    List<TransactionSummaryDto> transactions,

    List<CategorySummaryDto> categories
) {
}
