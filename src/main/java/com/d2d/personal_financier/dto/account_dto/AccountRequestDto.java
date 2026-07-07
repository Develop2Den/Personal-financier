package com.d2d.personal_financier.dto.account_dto;

import com.d2d.personal_financier.entity.enums.AccountType;
import com.d2d.personal_financier.entity.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Request for creating an account")
public record AccountRequestDto(

    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Account name must not exceed 100 characters")
    @Schema(description = "Account name", example = "Main Card")
    String name,

    @NotNull(message = "Currency is required")
    @Schema(description = "Account currency", example = "USD", allowableValues = {"UAH", "USD", "EUR"})
    Currency currency,

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Balance must be greater than or equal to 0")
    @Schema(description = "Initial account balance", example = "1000.00")
    BigDecimal balance,

    @NotNull(message = "Account type is required")
    @Schema(
        description = "Account type",
        example = "CARD",
        allowableValues = {"CASH", "CARD", "BANK", "CRYPTO"}
    )
    AccountType type

) {
}
