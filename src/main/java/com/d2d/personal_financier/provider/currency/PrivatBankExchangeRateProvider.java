package com.d2d.personal_financier.provider.currency;

import com.d2d.personal_financier.dto.currency_dto.ExchangeRateDto;
import com.d2d.personal_financier.dto.currency_dto.PrivatBankExchangeRateResponseDto;
import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PrivatBankExchangeRateProvider implements ExchangeRateProvider {

    private static final String PRIVATBANK_EXCHANGE_RATE_URL =
        "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";

    private final RestClient currencyRestClient;

    @Override
    public ExchangeRateSource getSource() {
        return ExchangeRateSource.PRIVATBANK;
    }

    @Override
    public Duration getCacheTtl() {
        return Duration.ofMinutes(5);
    }

    @Override
    public List<ExchangeRateDto> getExchangeRates() {

        List<PrivatBankExchangeRateResponseDto> rates =
            currencyRestClient
                .get()
                .uri(PRIVATBANK_EXCHANGE_RATE_URL)
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
        PrivatBankExchangeRateResponseDto rate) {

        if (!"UAH".equals(rate.base_ccy())) {
            return null;
        }

        Currency currency = switch (rate.ccy()) {
            case "USD" -> Currency.USD;
            case "EUR" -> Currency.EUR;
            default -> null;
        };

        if (currency == null
            || rate.buy() == null
            || rate.sale() == null) {
            return null;
        }

        return new ExchangeRateDto(
            currency,
            Currency.UAH,
            rate.buy(),
            rate.sale(),
            ExchangeRateSource.PRIVATBANK,
            LocalDateTime.now()
        );
    }
}
