package com.d2d.personal_financier.model;

import com.d2d.personal_financier.entity.enums.Currency;

public record ExchangeStep(
    Currency fromCurrency,
    Currency toCurrency
) {
}
