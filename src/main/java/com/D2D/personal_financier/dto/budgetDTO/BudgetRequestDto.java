package com.D2D.personal_financier.dto.budgetDTO;

import com.D2D.personal_financier.entity.enums.BudgetPeriod;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request for creating a budget")
public record BudgetRequestDto(

        @Schema(description = "Category ID for the budget", example = "2")
        Long categoryId,

        @Schema(description = "Budget limit amount", example = "500.00")
        BigDecimal limitAmount,

        @Schema(description = "Budget start date", example = "2026-04-01")
        LocalDate startDate,

        @Schema(description = "Budget end date", example = "2026-04-30")
        LocalDate endDate,

        @Schema(description = "Budget period", example = "MONTHLY")
        BudgetPeriod period
) {}
