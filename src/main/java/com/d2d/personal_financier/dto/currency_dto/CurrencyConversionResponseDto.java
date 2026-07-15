package com.d2d.personal_financier.dto.currency_dto;

import com.d2d.personal_financier.entity.enums.Currency;

import java.math.BigDecimal;
import java.util.List;

public record CurrencyConversionResponseDto(

    BigDecimal sourceAmount,

    Currency sourceCurrency,

    BigDecimal targetAmount,

    Currency targetCurrency,

    List<CurrencyConversionStepDto> steps

) {
}
