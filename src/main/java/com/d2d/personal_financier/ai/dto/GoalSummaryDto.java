package com.d2d.personal_financier.ai.dto;

import com.d2d.personal_financier.entity.enums.GoalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalSummaryDto(
    String name,

    BigDecimal targetAmount,

    BigDecimal currentAmount,

    LocalDate deadline,

    GoalStatus status
) {
}
