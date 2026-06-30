package com.d2d.personal_financier.ai.dto;

import java.math.BigDecimal;

public record FinancialMetricsDto(

    String analyzedGoalName,

    BigDecimal remainingGoalAmount,

    BigDecimal goalCompletionPercentage,

    BigDecimal largestExpense,

    BigDecimal largestIncome,

    BigDecimal averageExpense,

    String richestAccount,

    Integer activeAccounts

) {
}
