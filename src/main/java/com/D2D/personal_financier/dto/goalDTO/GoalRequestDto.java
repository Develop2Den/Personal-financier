package com.D2D.personal_financier.dto.goalDTO;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request for creating a financial goal")
public record GoalRequestDto(

        @Schema(description = "Goal name", example = "MacBook")
        String name,

        @Schema(description = "Target amount to reach", example = "2000.00")
        BigDecimal targetAmount,

        @Schema(description = "Goal deadline", example = "2026-12-31")
        LocalDate deadline
) {}
