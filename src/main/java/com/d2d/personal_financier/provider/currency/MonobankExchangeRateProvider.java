package com.d2d.personal_financier.provider.currency;

import com.d2d.personal_financier.dto.currency_dto.ExchangeRateDto;
import com.d2d.personal_financier.dto.currency_dto.MonobankExchangeRateResponseDto;
import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MonobankExchangeRateProvider implements ExchangeRateProvider {

    private static final String MONOBANK_EXCHANGE_RATE_URL =
        "https://api.monobank.ua/bank/currency";

    private static final int USD = 840;
    private static final int EUR = 978;
    private static final int UAH = 980;

    private final RestClient currencyRestClient;

    @Override
    public ExchangeRateSource getSource() {
        return ExchangeRateSource.MONOBANK;
    }

    @Override
    public Duration getCacheTtl() {
        return Duration.ofMinutes(5);
    }

    @Override
    public List<ExchangeRateDto> getExchangeRates() {

        List<MonobankExchangeRateResponseDto> rates =
            currencyRestClient
                .get()
                .uri(MONOBANK_EXCHANGE_RATE_URL)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        if (rates == null) {
            return List.of();
        }

        return rates.stream()
            .map(this::toExchangeRate)
            .filter(Objects::nonNull)
            .toList();
    }

    private ExchangeRateDto toExchangeRate(
        MonobankExchangeRateResponseDto rate) {

        if (rate.currencyCodeB() != UAH) {
            return null;
        }

        Currency currency = switch (rate.currencyCodeA()) {
            case USD -> Currency.USD;
            case EUR -> Currency.EUR;
            default -> null;
        };

        if (currency == null
            || rate.rateBuy() == null
            || rate.rateSell() == null) {
            return null;
        }

        return new ExchangeRateDto(
            currency,
            Currency.UAH,
            rate.rateBuy(),
            rate.rateSell(),
            ExchangeRateSource.MONOBANK,
            LocalDateTime.ofInstant(
                Instant.ofEpochSecond(rate.date()),
                ZoneId.systemDefault()
            )
        );
    }
}
