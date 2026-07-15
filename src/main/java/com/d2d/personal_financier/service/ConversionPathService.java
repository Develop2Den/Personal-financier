package com.d2d.personal_financier.service;


import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;
import com.d2d.personal_financier.model.ExchangePath;

public interface ConversionPathService {

    ExchangePath getPath(
        ExchangeRateSource source,
        Currency fromCurrency,
        Currency toCurrency
    );

}
