package com.d2d.personal_financier.provider.currency;

import com.d2d.personal_financier.dto.currency_dto.ExchangeRateDto;
import com.d2d.personal_financier.dto.currency_dto.NbuExchangeRateResponseDto;
import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NbuExchangeRateProvider implements ExchangeRateProvider {

    private final RestClient currencyRestClient;

    private static final String NBU_EXCHANGE_RATE_URL =
        "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";

    @Override
    public ExchangeRateSource getSource() {
        return ExchangeRateSource.NBU;
    }

    @Override
    public ExchangeRateDto getExchangeRate(
        Currency fromCurrency,
        Currency toCurrency) {

        List<NbuExchangeRateResponseDto> rates =
            currencyRestClient
                .get()
                .uri(NBU_EXCHANGE_RATE_URL)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return null;
    }
}
