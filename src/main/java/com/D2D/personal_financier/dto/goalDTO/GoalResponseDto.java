package com.D2D.personal_financier.dto.goalDTO;

import java.time.LocalDate;

public record GoalResponseDto(
        Long id,
        String name,
        Double targetAmount,
        Double currentAmount,
        LocalDate deadline
) {}
