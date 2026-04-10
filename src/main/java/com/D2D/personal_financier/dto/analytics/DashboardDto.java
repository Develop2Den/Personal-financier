package com.D2D.personal_financier.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Financial dashboard summary")
public record DashboardDto(

        @Schema(description = "Total balance across all accounts", example = "5400.00")
        BigDecimal totalBalance,

        @Schema(description = "Total expenses for the current period", example = "1200.00")
        BigDecimal monthlyExpenses,

        @Schema(description = "Category with highest spending", example = "Food")
        String topCategory,

        @Schema(description = "Number of active financial goals", example = "2")
        Long activeGoals

) {}
