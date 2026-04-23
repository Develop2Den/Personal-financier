package com.d2d.personal_financier.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Financial dashboard summary")
public record DashboardDto(

        @Schema(description = "Total balance across all accounts", example = "5400.00")
        BigDecimal totalBalance,

        @Schema(description = "Total income for the selected month", example = "2500.00")
        BigDecimal monthlyIncome,

        @Schema(description = "Total expenses for the selected month", example = "1200.00")
        BigDecimal monthlyExpenses,

        @Schema(description = "Net cashflow for the selected month", example = "1300.00")
        BigDecimal netCashflow,

        @Schema(description = "Category with highest spending in the selected month", example = "Food")
        String topExpenseCategory,

        @Schema(description = "Number of active financial goals", example = "2")
        Long activeGoals,

        @Schema(description = "Number of transactions in the selected month", example = "7")
        Long monthlyTransactionCount

) {}
