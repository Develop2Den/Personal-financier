package com.d2d.personal_financier.ai.dto;

import com.d2d.personal_financier.entity.enums.TransactionType;
import com.d2d.personal_financier.entity.enums.TransferDirection;
import com.d2d.personal_financier.entity.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Transaction summary used by AI assistant")
public record TransactionSummaryDto(
    
    @Schema(description = "Transaction date", example = "2026-04-10T18:30:00")
    LocalDateTime date,

    @Schema(description = "Transaction type", example = "EXPENSE")
    TransactionType type,

    @Schema(description = "Transaction amount", example = "250.50")
    BigDecimal amount,

    @Schema(description = "Transaction currency", example = "USD")
    Currency currency,

    @Schema(description = "Account name associated with transaction", example = "Main Card")
    String account,

    @Schema(description = "Category name associated with transaction", example = "Food")
    String category,

    @Schema(description = "Transaction description", example = "Dinner at restaurant")
    String description,

    @Schema(description = "Transfer direction", example = "OUTGOING")
    TransferDirection transferDirection
) {
}
