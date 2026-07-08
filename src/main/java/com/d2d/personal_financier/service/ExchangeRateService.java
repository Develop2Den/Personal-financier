package com.d2d.personal_financier.service;

import com.d2d.personal_financier.dto.currency_dto.ExchangeRateDto;
import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;
import com.d2d.personal_financier.exception.ExchangeRateException;
import com.d2d.personal_financier.provider.currency.ExchangeRateProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final List<ExchangeRateProvider> providers;

    public ExchangeRateDto getExchangeRate(
        Currency fromCurrency,
        Currency toCurrency,
        ExchangeRateSource source) {

        ExchangeRateProvider provider = providers.stream()
            .filter(p -> p.getSource() == source)
            .findFirst()
            .orElseThrow(() ->
                new ExchangeRateException("Exchange rate provider not found: " + source));

        return provider.getExchangeRate(fromCurrency, toCurrency);
    }

}
