package com.d2d.personal_financier.provider.currency;

import com.d2d.personal_financier.entity.enums.Currency;

import java.math.BigDecimal;

public interface ExchangeRateProvider {

    BigDecimal getExchangeRate(Currency from, Currency to);

}
