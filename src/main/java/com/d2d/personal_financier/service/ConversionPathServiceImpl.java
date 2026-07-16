package com.d2d.personal_financier.service;

import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.model.ExchangePath;
import com.d2d.personal_financier.model.ExchangeStep;
import com.d2d.personal_financier.service.interfaces.ConversionPathService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversionPathServiceImpl implements ConversionPathService {

    @Value("${app.currency.base}")
    private Currency baseCurrency;

    @Override
    public ExchangePath getPath(
        Currency fromCurrency,
        Currency toCurrency
    ) {

        if (fromCurrency == toCurrency) {
            return new ExchangePath(List.of());
        }

        if (fromCurrency == baseCurrency || toCurrency == baseCurrency) {
            return new ExchangePath(
                List.of(
                    new ExchangeStep(fromCurrency, toCurrency)
                )
            );
        }

        return new ExchangePath(
            List.of(
                new ExchangeStep(fromCurrency, baseCurrency),
                new ExchangeStep(baseCurrency, toCurrency)
            )
        );
    }
}
