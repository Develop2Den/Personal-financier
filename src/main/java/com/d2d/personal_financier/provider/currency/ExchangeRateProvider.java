package com.d2d.personal_financier.provider.currency;

import com.d2d.personal_financier.dto.currency_dto.ExchangeRateDto;
import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;

public interface ExchangeRateProvider {

    ExchangeRateSource getSource();

    ExchangeRateDto getExchangeRate(
        Currency fromCurrency,
        Currency toCurrency
    );
}
