package com.d2d.personal_financier.provider.currency;

import com.d2d.personal_financier.dto.currency_dto.ExchangeRateDto;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;

import java.time.Duration;
import java.util.List;

public interface ExchangeRateProvider {

    ExchangeRateSource getSource();

    Duration getCacheTtl();

    List<ExchangeRateDto> getExchangeRates();
}
