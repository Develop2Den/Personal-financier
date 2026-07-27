package com.d2d.personal_financier.ai.dto;

import com.d2d.personal_financier.dto.analytics.DashboardDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Financial context used by AI assistant")
public record FinancialContextDto(

    @Schema(description = "Financial dashboard summary", example = "{\"totalBalance\":5400.00,\"monthlyIncome\":2500.00}")
    DashboardDto dashboard,

    @Schema(description = "Calculated financial metrics", example = "{\"remainingGoalAmount\":1500.00,\"largestExpense\":450.00}")
    FinancialMetricsDto metrics,

    @Schema(description = "Accounts available to user", example = "[{\"name\":\"Main Card\",\"type\":\"CARD\",\"balance\":850.25,\"currency\":\"USD\"}]")
    List<AccountSummaryDto> accounts,

    @Schema(description = "Financial goals available to user", example = "[{\"name\":\"Emergency fund\",\"targetAmount\":5000.00}]")
    List<GoalSummaryDto> goals,

    @Schema(description = "Recent transactions", example = "[{\"type\":\"EXPENSE\",\"amount\":250.50,\"currency\":\"USD\"}]")
    List<TransactionSummaryDto> transactions,

    @Schema(description = "Categories available to user", example = "[{\"name\":\"Food\",\"type\":\"EXPENSE\",\"active\":true}]")
    List<CategorySummaryDto> categories
) {
}
