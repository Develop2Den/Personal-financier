package com.d2d.personal_financier.ai.dto;

import com.d2d.personal_financier.entity.enums.TransactionType;

public record CategorySummaryDto(
    String name,

    TransactionType type,

    Boolean active
) {
}
