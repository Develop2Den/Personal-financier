package com.d2d.personal_financier.service;

import com.d2d.personal_financier.dto.currency_dto.CurrencyConversionRequestDto;
import com.d2d.personal_financier.dto.currency_dto.CurrencyConversionResponseDto;
import com.d2d.personal_financier.dto.currency_dto.CurrencyConversionStepDto;
import com.d2d.personal_financier.dto.currency_dto.ExchangeRateDto;
import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.entity.enums.ExchangeOperation;
import com.d2d.personal_financier.exception.ExchangeRateException;
import com.d2d.personal_financier.model.ExchangePath;
import com.d2d.personal_financier.model.ExchangeStep;
import com.d2d.personal_financier.service.interfaces.ConversionPathService;
import com.d2d.personal_financier.service.interfaces.CurrencyConversionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;
    private static final int FINAL_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private final ConversionPathService conversionPathService;
    private final ExchangeRateService exchangeRateService;
    private final Currency baseCurrency;

    public CurrencyConversionServiceImpl(
        ConversionPathService conversionPathService,
        ExchangeRateService exchangeRateService,
        @Value("${app.currency.base}") Currency baseCurrency
    ) {
        this.conversionPathService = conversionPathService;
        this.exchangeRateService = exchangeRateService;
        this.baseCurrency = baseCurrency;
    }

    @Override
    public CurrencyConversionResponseDto convert(
        CurrencyConversionRequestDto request
    ) {

        ExchangePath path = conversionPathService.getPath(
            request.fromCurrency(),
            request.toCurrency()
        );

        BigDecimal currentAmount = request.amount();
        List<CurrencyConversionStepDto> steps = new ArrayList<>();

        for (ExchangeStep step : path.steps()) {

            CurrencyConversionStepDto conversionStep = convertStep(
                request,
                step,
                currentAmount
            );

            steps.add(conversionStep);
            currentAmount = conversionStep.targetAmount();
        }

        return new CurrencyConversionResponseDto(
            request.amount(),
            request.fromCurrency(),
            roundFinalAmount(currentAmount),
            request.toCurrency(),
            List.copyOf(steps)
        );
    }

    private CurrencyConversionStepDto convertStep(
        CurrencyConversionRequestDto request,
        ExchangeStep step,
        BigDecimal sourceAmount
    ) {

        ExchangeRateDto rate = exchangeRateService.getExchangeRate(
            request.source(),
            step.fromCurrency(),
            step.toCurrency()
        );

        ExchangeOperation operation = resolveOperation(step);
        BigDecimal appliedRate = resolveAppliedRate(rate, operation);

        validateRate(
            rate,
            operation,
            step
        );

        BigDecimal targetAmount = calculateTargetAmount(
            sourceAmount,
            appliedRate,
            operation
        );

        return new CurrencyConversionStepDto(
            step.fromCurrency(),
            step.toCurrency(),
            appliedRate,
            sourceAmount,
            targetAmount,
            operation,
            rate.source(),
            rate.updatedAt()
        );
    }

    private ExchangeOperation resolveOperation(
        ExchangeStep step
    ) {

        if (step.fromCurrency() == baseCurrency) {
            return ExchangeOperation.BUY;
        }

        if (step.toCurrency() == baseCurrency) {
            return ExchangeOperation.SELL;
        }

        throw new ExchangeRateException(
            "Cannot resolve exchange operation for conversion step: "
                + step.fromCurrency()
                + " -> "
                + step.toCurrency()
        );
    }

    private BigDecimal resolveAppliedRate(
        ExchangeRateDto rate,
        ExchangeOperation operation
    ) {

        return switch (operation) {
            case SELL -> rate.buyRate();
            case BUY -> rate.sellRate();
        };
    }

    private void validateRate(
        ExchangeRateDto rate,
        ExchangeOperation operation,
        ExchangeStep step
    ) {

        BigDecimal appliedRate = resolveAppliedRate(
            rate,
            operation
        );

        if (appliedRate == null || appliedRate.compareTo(BigDecimal.ZERO) <= 0) {

            throw new ExchangeRateException(
                "Invalid "
                    + operation
                    + " exchange rate for "
                    + step.fromCurrency()
                    + " -> "
                    + step.toCurrency()
            );
        }
    }

    private BigDecimal calculateTargetAmount(
        BigDecimal sourceAmount,
        BigDecimal appliedRate,
        ExchangeOperation operation
    ) {

        return switch (operation) {
            case SELL -> sourceAmount.multiply(
                appliedRate,
                MATH_CONTEXT
            );

            case BUY -> sourceAmount.divide(
                appliedRate,
                MATH_CONTEXT
            );
        };
    }

    private BigDecimal roundFinalAmount(
        BigDecimal amount
    ) {

        return amount.setScale(
            FINAL_SCALE,
            ROUNDING_MODE
        );
    }

}
