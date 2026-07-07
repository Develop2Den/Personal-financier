package com.d2d.personal_financier.ai.dto;

import java.math.BigDecimal;

public record FinancialMetricsDto(

    // Goal Metrics
    String analyzedGoalName,
    BigDecimal remainingGoalAmount,
    BigDecimal goalCompletionPercentage,

    // Transaction Metrics
    BigDecimal largestExpense,
    BigDecimal largestIncome,
    BigDecimal averageExpense,

    // Account Metrics
    String richestAccount,
    Integer activeAccounts

) {
}
