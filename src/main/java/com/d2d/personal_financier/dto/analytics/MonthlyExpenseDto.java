package com.d2d.personal_financier.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Monthly expenses report")
public record MonthlyExpenseDto(

        @Schema(description = "Month in format YYYY-MM", example = "2026-03")
        String month,

        @Schema(description = "Total expenses for the month", example = "1200.00")
        BigDecimal total

) {}
