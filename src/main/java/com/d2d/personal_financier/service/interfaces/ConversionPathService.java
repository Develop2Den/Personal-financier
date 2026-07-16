package com.d2d.personal_financier.service.interfaces;


import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.model.ExchangePath;

public interface ConversionPathService {

    ExchangePath getPath(
        Currency fromCurrency,
        Currency toCurrency
    );

}
