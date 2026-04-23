package com.d2d.personal_financier.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Monthly cashflow summary")
public record MonthlyCashflowDto(

    @Schema(description = "Month in format YYYY-MM", example = "2026-04")
    String month,

    @Schema(description = "Total income for the month", example = "2000.00")
    BigDecimal income,

    @Schema(description = "Total expenses for the month", example = "1200.00")
    BigDecimal expenses,

    @Schema(description = "Net cashflow for the month", example = "800.00")
    BigDecimal net

) {}
