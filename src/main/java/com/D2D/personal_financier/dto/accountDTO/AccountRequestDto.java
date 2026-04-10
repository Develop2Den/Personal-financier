package com.D2D.personal_financier.dto.accountDTO;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Request for creating an account")
public record AccountRequestDto(

        @Schema(description = "Account name", example = "Main Card")
        String name,

        @Schema(description = "Account currency", example = "USD")
        String currency,

        @Schema(description = "Initial account balance", example = "1000.00")
        BigDecimal balance

) {}
