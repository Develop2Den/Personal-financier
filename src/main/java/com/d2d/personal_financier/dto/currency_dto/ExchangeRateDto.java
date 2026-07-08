package com.d2d.personal_financier.dto.currency_dto;

import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExchangeRateDto(

    Currency fromCurrency,

    Currency toCurrency,

    BigDecimal buyRate,

    BigDecimal sellRate,

    ExchangeRateSource source,

    LocalDateTime updatedAt

) {
}
