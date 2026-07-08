package com.d2d.personal_financier.provider.currency;

import com.d2d.personal_financier.dto.currency_dto.ExchangeRateDto;
import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class MockExchangeRateProvider implements ExchangeRateProvider {

    @Override
    public ExchangeRateSource getSource() {
        return ExchangeRateSource.NBU;
    }

    @Override
    public ExchangeRateDto getExchangeRate(
        Currency fromCurrency,
        Currency toCurrency) {

        return new ExchangeRateDto(
            fromCurrency,
            toCurrency,
            new BigDecimal("41.20"),
            new BigDecimal("41.70"),
            getSource(),
            LocalDateTime.now()
        );
    }
}
