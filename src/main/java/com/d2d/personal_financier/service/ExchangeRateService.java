package com.d2d.personal_financier.service;

import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.provider.currency.ExchangeRateProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateProvider exchangeRateProvider;

    public BigDecimal getExchangeRate(Currency from, Currency to) {

        if (from == to) {
            return BigDecimal.ONE;
        }

        return exchangeRateProvider.getExchangeRate(from, to);
    }
}
