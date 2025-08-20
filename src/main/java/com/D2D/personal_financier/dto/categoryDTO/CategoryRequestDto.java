package com.D2D.personal_financier.dto.categoryDTO;

import com.D2D.personal_financier.entity.enums.TransactionType;

public record CategoryRequestDto(
        String name,
        TransactionType type
) {}
