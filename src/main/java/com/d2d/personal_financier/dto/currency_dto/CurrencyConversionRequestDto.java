package com.d2d.personal_financier.dto.currency_dto;

import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CurrencyConversionRequestDto(

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    BigDecimal amount,

    @NotNull(message = "Source currency is required")
    Currency fromCurrency,

    @NotNull(message = "Target currency is required")
    Currency toCurrency,

    @NotNull(message = "Exchange rate source is required")
    ExchangeRateSource source

) {
}
