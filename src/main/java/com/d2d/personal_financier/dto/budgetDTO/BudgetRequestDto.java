package com.d2d.personal_financier.dto.budgetDTO;

import com.d2d.personal_financier.entity.enums.BudgetPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request for creating a budget")
public record BudgetRequestDto(

        @NotNull(message = "Category id is required")
        @Positive(message = "Category id must be positive")
        @Schema(description = "Category ID for the budget", example = "2")
        Long categoryId,

        @NotNull(message = "Budget limit is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Budget limit must be greater than 0")
        @Schema(description = "Budget limit amount", example = "500.00")
        BigDecimal limitAmount,

        @NotNull(message = "Budget start date is required")
        @Schema(description = "Budget start date", example = "2026-04-01")
        LocalDate startDate,

        @NotNull(message = "Budget end date is required")
        @Schema(description = "Budget end date", example = "2026-04-30")
        LocalDate endDate,

        @NotNull(message = "Budget period is required")
        @Schema(description = "Budget period", example = "MONTHLY")
        BudgetPeriod period
) {

    @AssertTrue(message = "Budget end date must be on or after start date")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }
}
