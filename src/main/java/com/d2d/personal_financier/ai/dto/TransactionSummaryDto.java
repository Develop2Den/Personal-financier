package com.d2d.personal_financier.ai.dto;

import com.d2d.personal_financier.entity.enums.TransactionType;
import com.d2d.personal_financier.entity.enums.TransferDirection;
import com.d2d.personal_financier.entity.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionSummaryDto(
    
    LocalDateTime date,

    TransactionType type,

    BigDecimal amount,

    Currency currency,

    String account,

    String category,

    String description,

    TransferDirection transferDirection
) {
}
