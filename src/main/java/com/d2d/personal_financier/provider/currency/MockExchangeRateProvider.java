package com.d2d.personal_financier.provider.currency;


import com.d2d.personal_financier.dto.currency_dto.ExchangeRateDto;
import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class MockExchangeRateProvider implements ExchangeRateProvider {

    @Override
    public ExchangeRateSource getSource() {
        return ExchangeRateSource.MOCK;
    }

    @Override
    public Duration getCacheTtl() {
        return Duration.ofHours(24);
    }

    @Override
    public List<ExchangeRateDto> getExchangeRates() {

        return List.of(
            new ExchangeRateDto(
                Currency.USD,
                Currency.UAH,
                new BigDecimal("41.20"),
                new BigDecimal("41.70"),
                getSource(),
                LocalDateTime.now()
            ),
            new ExchangeRateDto(
                Currency.EUR,
                Currency.UAH,
                new BigDecimal("48.10"),
                new BigDecimal("48.60"),
                getSource(),
                LocalDateTime.now()
            )
        );
    }
}
