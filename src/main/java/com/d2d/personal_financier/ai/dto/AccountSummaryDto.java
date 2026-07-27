package com.d2d.personal_financier.ai.dto;

import com.d2d.personal_financier.entity.enums.AccountType;
import com.d2d.personal_financier.entity.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Account summary used by AI assistant")
public record AccountSummaryDto(

    @Schema(description = "Account name", example = "Main Card")
    String name,

    @Schema(description = "Account type", example = "CARD")
    AccountType type,

    @Schema(description = "Current account balance", example = "850.25")
    BigDecimal balance,

    @Schema(description = "Account currency", example = "USD")
    Currency currency

) {
}
