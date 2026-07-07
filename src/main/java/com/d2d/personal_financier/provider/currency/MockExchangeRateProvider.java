package com.d2d.personal_financier.provider.currency;

import com.d2d.personal_financier.entity.enums.Currency;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MockExchangeRateProvider implements ExchangeRateProvider {

    @Override
    public BigDecimal getExchangeRate(Currency from, Currency to) {

        return switch (from) {

            case USD -> switch (to) {
                case UAH -> BigDecimal.valueOf(41.50);
                case EUR -> BigDecimal.valueOf(0.86);
                default -> BigDecimal.ONE;
            };

            case EUR -> switch (to) {
                case UAH -> BigDecimal.valueOf(48.20);
                case USD -> BigDecimal.valueOf(1.16);
                default -> BigDecimal.ONE;
            };

            case UAH -> switch (to) {
                case USD -> BigDecimal.valueOf(0.024);
                case EUR -> BigDecimal.valueOf(0.021);
                default -> BigDecimal.ONE;
            };
        };
    }
}
