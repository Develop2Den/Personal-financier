package com.D2D.personal_financier.dto.goalDTO;

import java.time.LocalDate;

public record GoalRequestDto(
        String name,
        Double targetAmount,
        LocalDate deadline
) {}
