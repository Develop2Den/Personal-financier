package com.D2D.personal_financier.dto.budgetDTO;

import com.D2D.personal_financier.entity.enums.BudgetPeriod;

import java.time.LocalDate;

public record BudgetRequestDto(
        Long categoryId,
        Double limitAmount,
        LocalDate startDate,
        LocalDate endDate,
        BudgetPeriod period
) {}
