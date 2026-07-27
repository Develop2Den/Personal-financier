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
        ExchangeRateSource source
    ) {

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
        Currency toCurrency
    ) {

        List<ExchangeRateDto> rates = getExchangeRates(source);

        ExchangeRateDto directRate = findDirectRate(
            rates,
            fromCurrency,
            toCurrency
        );

        if (directRate != null) {
            return directRate;
        }

        ExchangeRateDto reverseRate = findReverseRate(
            rates,
            fromCurrency,
            toCurrency
        );

        if (reverseRate != null) {
            return reverseRate;
        }

        throw new ExchangeRateException(
            "Exchange rate not found: "
                + fromCurrency
                + " -> "
                + toCurrency
                + " (" + source + ")"
        );
    }

    private ExchangeRateDto findDirectRate(
        List<ExchangeRateDto> rates,
        Currency fromCurrency,
        Currency toCurrency
    ) {

        return rates.stream()
            .filter(rate ->
                rate.fromCurrency() == fromCurrency
                    && rate.toCurrency() == toCurrency
            )
            .findFirst()
            .orElse(null);
    }

    private ExchangeRateDto findReverseRate(
        List<ExchangeRateDto> rates,
        Currency fromCurrency,
        Currency toCurrency
    ) {

        return rates.stream()
            .filter(rate ->
                rate.fromCurrency() == toCurrency
                    && rate.toCurrency() == fromCurrency
            )
            .findFirst()
            .map(this::reverse)
            .orElse(null);
    }

    private ExchangeRateDto reverse(
        ExchangeRateDto rate
    ) {

        return new ExchangeRateDto(
            rate.toCurrency(),
            rate.fromCurrency(),
            rate.buyRate(),
            rate.sellRate(),
            rate.source(),
            rate.updatedAt()
        );
    }

    private ExchangeRateProvider getProvider(
        ExchangeRateSource source
    ) {

        return providers.stream()
            .filter(provider -> provider.getSource() == source)
            .findFirst()
            .orElseThrow(() ->
                new ExchangeRateException(
                    "Exchange rate provider not found: " + source
                )
            );
    }

    public List<ExchangeRateDto> getAllExchangeRates() {

        return providers.stream()
            .map(ExchangeRateProvider::getSource)
            .distinct()
            .flatMap(source ->
                getExchangeRates(source).stream()
            )
            .toList();
    }
}
