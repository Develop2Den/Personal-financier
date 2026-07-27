package com.d2d.personal_financier.ai.dto;

import com.d2d.personal_financier.entity.enums.GoalStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Financial goal summary used by AI assistant")
public record GoalSummaryDto(
    @Schema(description = "Goal name", example = "Emergency fund")
    String name,

    @Schema(description = "Target amount to reach", example = "5000.00")
    BigDecimal targetAmount,

    @Schema(description = "Current amount saved for goal", example = "1500.00")
    BigDecimal currentAmount,

    @Schema(description = "Goal deadline", example = "2026-12-31")
    LocalDate deadline,

    @Schema(description = "Goal status", example = "ACTIVE")
    GoalStatus status
) {
}
