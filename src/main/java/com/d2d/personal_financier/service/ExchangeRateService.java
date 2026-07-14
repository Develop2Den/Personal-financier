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
    private final ExchangeRateCacheService cacheService;

    public List<ExchangeRateDto> getExchangeRates(
        ExchangeRateSource source) {

        List<ExchangeRateDto> cachedRates = cacheService.getRates(source);

        if (!cachedRates.isEmpty()) {
            return cachedRates;
        }

        ExchangeRateProvider provider = getProvider(source);

        List<ExchangeRateDto> rates = provider.getExchangeRates();

        cacheService.saveRates(
            source,
            rates,
            provider.getCacheTtl()
        );

        return rates;
    }

    public ExchangeRateDto getExchangeRate(
        ExchangeRateSource source,
        Currency fromCurrency,
        Currency toCurrency) {

        return getExchangeRates(source).stream()
            .filter(rate ->
                rate.fromCurrency() == fromCurrency
                    && rate.toCurrency() == toCurrency)
            .findFirst()
            .orElseThrow(() ->
                new ExchangeRateException(
                    "Exchange rate not found: "
                        + fromCurrency
                        + " -> "
                        + toCurrency
                        + " (" + source + ")"));
    }

    private ExchangeRateProvider getProvider(
        ExchangeRateSource source) {

        return providers.stream()
            .filter(provider -> provider.getSource() == source)
            .findFirst()
            .orElseThrow(() ->
                new ExchangeRateException(
                    "Exchange rate provider not found: " + source));
    }
}
