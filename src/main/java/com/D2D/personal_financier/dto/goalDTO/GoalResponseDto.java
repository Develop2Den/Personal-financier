package com.D2D.personal_financier.dto.goalDTO;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Response with goal information")
public record GoalResponseDto(

        @Schema(description = "Goal ID", example = "1")
        Long id,

        @Schema(description = "Goal name", example = "MacBook")
        String name,

        @Schema(description = "Target amount", example = "2000.00")
        BigDecimal targetAmount,

        @Schema(description = "Current saved amount", example = "600.00")
        BigDecimal currentAmount,

        @Schema(description = "Progress in percent", example = "30")
        Integer progress,

        @Schema(description = "Goal deadline", example = "2026-12-31")
        LocalDate deadline
) {}
