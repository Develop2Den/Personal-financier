package com.D2D.personal_financier.dto.categoryDTO;

import com.D2D.personal_financier.entity.enums.TransactionType;

public record CategoryResponseDto(
        Long id,
        String name,
        TransactionType type
) {}
