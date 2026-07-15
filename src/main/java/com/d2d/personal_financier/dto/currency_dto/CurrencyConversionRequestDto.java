package com.d2d.personal_financier.dto.currency_dto;

import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;

import java.math.BigDecimal;

public record CurrencyConversionRequestDto(

    BigDecimal amount,

    Currency fromCurrency,

    Currency toCurrency,

    ExchangeRateSource source

) {
}
