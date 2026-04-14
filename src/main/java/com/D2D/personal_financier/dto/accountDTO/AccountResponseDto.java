package com.D2D.personal_financier.dto.accountDTO;

import com.D2D.personal_financier.entity.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Response with account information")
public record AccountResponseDto(

        @Schema(description = "Account ID", example = "1")
        Long id,

        @Schema(description = "Account name", example = "Main Card")
        String name,

        @Schema(description = "Account currency", example = "USD")
        String currency,

        @Schema(description = "Current account balance", example = "850.25")
        BigDecimal balance,

        @Schema(description = "Type of account (CASH, CARD, BANK, CRYPTO)", example = "CARD")
        AccountType type

) {}
