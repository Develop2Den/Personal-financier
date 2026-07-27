package com.d2d.personal_financier.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Calculated financial metrics for AI assistant")
public record FinancialMetricsDto(

    // Goal Metrics
    @Schema(description = "Analyzed financial goal name", example = "Emergency fund")
    String analyzedGoalName,
    @Schema(description = "Remaining amount required to reach goal", example = "1500.00")
    BigDecimal remainingGoalAmount,
    @Schema(description = "Goal completion percentage", example = "25.00")
    BigDecimal goalCompletionPercentage,

    // Transaction Metrics
    @Schema(description = "Largest expense amount", example = "450.00")
    BigDecimal largestExpense,
    @Schema(description = "Largest income amount", example = "2500.00")
    BigDecimal largestIncome,
    @Schema(description = "Average expense amount", example = "120.50")
    BigDecimal averageExpense,

    // Account Metrics
    @Schema(description = "Account with highest balance", example = "Main Card")
    String richestAccount,
    @Schema(description = "Number of active accounts", example = "2")
    Integer activeAccounts

) {
}
