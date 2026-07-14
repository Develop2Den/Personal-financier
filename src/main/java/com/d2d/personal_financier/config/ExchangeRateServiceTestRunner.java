package com.d2d.personal_financier.config;

import com.d2d.personal_financier.dto.currency_dto.ExchangeRateDto;
import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;
import com.d2d.personal_financier.service.ExchangeRateCacheService;
import com.d2d.personal_financier.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeRateServiceTestRunner implements CommandLineRunner {

    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateCacheService exchangeRateCacheService;

    @Override
    public void run(String... args) {

        exchangeRateCacheService.evictRates(ExchangeRateSource.PRIVATBANK);

        log.info("========== FIRST CALL ==========");

        ExchangeRateDto usd = exchangeRateService.getExchangeRate(
            ExchangeRateSource.PRIVATBANK,
            Currency.USD,
            Currency.UAH
        );

        log.info("{}", usd);

        log.info("========== SECOND CALL ==========");

        ExchangeRateDto usdCached = exchangeRateService.getExchangeRate(
            ExchangeRateSource.PRIVATBANK,
            Currency.USD,
            Currency.UAH
        );

        log.info("{}", usdCached);
    }
}
