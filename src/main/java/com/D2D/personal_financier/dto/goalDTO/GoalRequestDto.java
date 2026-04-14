package com.D2D.personal_financier.dto.goalDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request for creating a financial goal")
public record GoalRequestDto(

        @NotBlank(message = "Goal name is required")
        @Size(max = 100, message = "Goal name must not exceed 100 characters")
        @Schema(description = "Goal name", example = "MacBook")
        String name,

        @NotNull(message = "Target amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Target amount must be greater than 0")
        @Schema(description = "Target amount to reach", example = "2000.00")
        BigDecimal targetAmount,

        @FutureOrPresent(message = "Goal deadline must be today or in the future")
        @Schema(description = "Goal deadline", example = "2026-12-31")
        LocalDate deadline
) {}
