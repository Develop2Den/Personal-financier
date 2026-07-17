package com.d2d.personal_financier.service;

import com.d2d.personal_financier.dto.currency_dto.CurrencyConversionRequestDto;
import com.d2d.personal_financier.dto.currency_dto.CurrencyConversionResponseDto;
import com.d2d.personal_financier.dto.currency_dto.CurrencyConversionStepDto;
import com.d2d.personal_financier.dto.currency_dto.ExchangeRateDto;
import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.entity.enums.ExchangeOperation;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;
import com.d2d.personal_financier.exception.ExchangeRateException;
import com.d2d.personal_financier.model.ExchangePath;
import com.d2d.personal_financier.model.ExchangeStep;
import com.d2d.personal_financier.service.interfaces.ConversionPathService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyConversionServiceImplTest {

    private static final LocalDateTime UPDATED_AT =
        LocalDateTime.of(
            2026,
            Month.JULY,
            10,
            12,
            0
        );
    private Validator validator;

    @Mock
    private ConversionPathService conversionPathService;

    @Mock
    private ExchangeRateService exchangeRateService;

    private CurrencyConversionServiceImpl currencyConversionService;

    @BeforeEach
    void setUp() {
        currencyConversionService = new CurrencyConversionServiceImpl(
            conversionPathService,
            exchangeRateService,
            Currency.UAH
        );
        validator = Validation
            .buildDefaultValidatorFactory()
            .getValidator();
    }

    @Test
    void convertShouldBuyUsdForUah() {
        CurrencyConversionRequestDto request = request("1000.00", Currency.UAH, Currency.USD);
        ExchangeStep step = new ExchangeStep(Currency.UAH, Currency.USD);
        ExchangeRateDto rate = rate(Currency.USD, Currency.UAH, "40.00", "41.00");

        when(conversionPathService.getPath(Currency.UAH, Currency.USD))
            .thenReturn(new ExchangePath(List.of(step)));
        when(exchangeRateService.getExchangeRate(ExchangeRateSource.MOCK, Currency.UAH, Currency.USD))
            .thenReturn(rate);

        CurrencyConversionResponseDto response = currencyConversionService.convert(request);

        assertEquals(new BigDecimal("24.39"), response.targetAmount());
        assertEquals(Currency.UAH, response.sourceCurrency());
        assertEquals(Currency.USD, response.targetCurrency());
        assertEquals(1, response.steps().size());

        CurrencyConversionStepDto conversionStep = response.steps().getFirst();
        assertEquals(new BigDecimal("41.00"), conversionStep.appliedRate());
        assertEquals(ExchangeOperation.BUY, conversionStep.operation());
        assertEquals(new BigDecimal("1000.00"), conversionStep.sourceAmount());
        assertEquals(0, new BigDecimal("24.39024390243902439024390243902439")
            .compareTo(conversionStep.targetAmount()));
    }

    @Test
    void convertShouldSellUsdForUah() {
        CurrencyConversionRequestDto request = request("10.00", Currency.USD, Currency.UAH);
        ExchangeStep step = new ExchangeStep(Currency.USD, Currency.UAH);
        ExchangeRateDto rate = rate(Currency.USD, Currency.UAH, "40.00", "41.00");

        when(conversionPathService.getPath(Currency.USD, Currency.UAH))
            .thenReturn(new ExchangePath(List.of(step)));
        when(exchangeRateService.getExchangeRate(ExchangeRateSource.MOCK, Currency.USD, Currency.UAH))
            .thenReturn(rate);

        CurrencyConversionResponseDto response = currencyConversionService.convert(request);

        assertEquals(new BigDecimal("400.00"), response.targetAmount());
        CurrencyConversionStepDto conversionStep = response.steps().getFirst();
        assertEquals(new BigDecimal("40.00"), conversionStep.appliedRate());
        assertEquals(ExchangeOperation.SELL, conversionStep.operation());
        assertEquals(new BigDecimal("400.0000"), conversionStep.targetAmount());
        assertEquals(ExchangeRateSource.MOCK, conversionStep.source());
        assertEquals(UPDATED_AT, conversionStep.rateUpdatedAt());
    }

    @Test
    void convertShouldConvertUsdToEurThroughBaseCurrency() {
        CurrencyConversionRequestDto request = request("10.00", Currency.USD, Currency.EUR);
        ExchangeStep firstStep = new ExchangeStep(Currency.USD, Currency.UAH);
        ExchangeStep secondStep = new ExchangeStep(Currency.UAH, Currency.EUR);

        when(conversionPathService.getPath(Currency.USD, Currency.EUR))
            .thenReturn(new ExchangePath(List.of(firstStep, secondStep)));
        when(exchangeRateService.getExchangeRate(ExchangeRateSource.MOCK, Currency.USD, Currency.UAH))
            .thenReturn(rate(Currency.USD, Currency.UAH, "40.00", "41.00"));
        when(exchangeRateService.getExchangeRate(ExchangeRateSource.MOCK, Currency.UAH, Currency.EUR))
            .thenReturn(rate(Currency.EUR, Currency.UAH, "49.00", "50.00"));

        CurrencyConversionResponseDto response = currencyConversionService.convert(request);

        assertEquals(new BigDecimal("8.00"), response.targetAmount());
        assertEquals(2, response.steps().size());
        assertEquals(ExchangeOperation.SELL, response.steps().get(0).operation());
        assertEquals(new BigDecimal("400.0000"), response.steps().get(0).targetAmount());
        assertEquals(ExchangeOperation.BUY, response.steps().get(1).operation());
        assertEquals(0, new BigDecimal("8.0000")
            .compareTo(response.steps().get(1).targetAmount()));
    }

    @Test
    void convertShouldConvertEurToUsdThroughBaseCurrency() {
        CurrencyConversionRequestDto request = request("10.00", Currency.EUR, Currency.USD);
        ExchangeStep firstStep = new ExchangeStep(Currency.EUR, Currency.UAH);
        ExchangeStep secondStep = new ExchangeStep(Currency.UAH, Currency.USD);

        when(conversionPathService.getPath(Currency.EUR, Currency.USD))
            .thenReturn(new ExchangePath(List.of(firstStep, secondStep)));
        when(exchangeRateService.getExchangeRate(ExchangeRateSource.MOCK, Currency.EUR, Currency.UAH))
            .thenReturn(rate(Currency.EUR, Currency.UAH, "49.00", "50.00"));
        when(exchangeRateService.getExchangeRate(ExchangeRateSource.MOCK, Currency.UAH, Currency.USD))
            .thenReturn(rate(Currency.USD, Currency.UAH, "40.00", "41.00"));

        CurrencyConversionResponseDto response = currencyConversionService.convert(request);

        assertEquals(new BigDecimal("11.95"), response.targetAmount());
        assertEquals(2, response.steps().size());
        assertEquals(new BigDecimal("490.0000"), response.steps().get(0).targetAmount());
        assertEquals(0, new BigDecimal("11.95121951219512195121951219512195")
            .compareTo(response.steps().get(1).targetAmount()));
    }

    @Test
    void convertShouldReturnSameAmountForSameCurrency() {
        CurrencyConversionRequestDto request = request("10.005", Currency.USD, Currency.USD);

        when(conversionPathService.getPath(Currency.USD, Currency.USD))
            .thenReturn(new ExchangePath(List.of()));

        CurrencyConversionResponseDto response = currencyConversionService.convert(request);

        assertEquals(new BigDecimal("10.01"), response.targetAmount());
        assertEquals(new BigDecimal("10.005"), response.sourceAmount());
        assertEquals(List.of(), response.steps());
        verify(exchangeRateService, never()).getExchangeRate(
            ExchangeRateSource.MOCK,
            Currency.USD,
            Currency.USD
        );
    }

    @Test
    void convertShouldPropagateMissingExchangeRate() {
        CurrencyConversionRequestDto request = request("10.00", Currency.USD, Currency.UAH);
        ExchangeStep step = new ExchangeStep(Currency.USD, Currency.UAH);

        when(conversionPathService.getPath(Currency.USD, Currency.UAH))
            .thenReturn(new ExchangePath(List.of(step)));
        when(exchangeRateService.getExchangeRate(ExchangeRateSource.MOCK, Currency.USD, Currency.UAH))
            .thenThrow(new ExchangeRateException("Exchange rate not found"));

        assertThrows(ExchangeRateException.class, () -> currencyConversionService.convert(request));
    }

    @Test
    void requestShouldRejectInvalidAmount() {
        CurrencyConversionRequestDto request = request("0.00", Currency.USD, Currency.UAH);

        Set<ConstraintViolation<CurrencyConversionRequestDto>> violations =
            validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("Amount must be greater than 0", violations.iterator().next().getMessage());
    }

    @Test
    void convertShouldRejectZeroAppliedRate() {
        CurrencyConversionRequestDto request = request("1000.00", Currency.UAH, Currency.USD);
        ExchangeStep step = new ExchangeStep(Currency.UAH, Currency.USD);
        ExchangeRateDto rate = rate(Currency.USD, Currency.UAH, "40.00", "0.00");

        when(conversionPathService.getPath(Currency.UAH, Currency.USD))
            .thenReturn(new ExchangePath(List.of(step)));
        when(exchangeRateService.getExchangeRate(ExchangeRateSource.MOCK, Currency.UAH, Currency.USD))
            .thenReturn(rate);

        ExchangeRateException exception = assertThrows(
            ExchangeRateException.class,
            () -> currencyConversionService.convert(request)
        );

        assertEquals("Invalid BUY exchange rate for UAH -> USD", exception.getMessage());
    }

    private CurrencyConversionRequestDto request(
        String amount,
        Currency fromCurrency,
        Currency toCurrency
    ) {
        return new CurrencyConversionRequestDto(
            new BigDecimal(amount),
            fromCurrency,
            toCurrency,
            ExchangeRateSource.MOCK
        );
    }

    private ExchangeRateDto rate(
        Currency fromCurrency,
        Currency toCurrency,
        String buyRate,
        String sellRate
    ) {
        return new ExchangeRateDto(
            fromCurrency,
            toCurrency,
            new BigDecimal(buyRate),
            new BigDecimal(sellRate),
            ExchangeRateSource.MOCK,
            UPDATED_AT
        );
    }

    @Test
    void convertShouldKeepIntermediatePrecision() {

        CurrencyConversionRequestDto request =
            request("1000.00", Currency.UAH, Currency.USD);

        ExchangeStep step =
            new ExchangeStep(Currency.UAH, Currency.USD);

        when(conversionPathService.getPath(Currency.UAH, Currency.USD))
            .thenReturn(new ExchangePath(List.of(step)));

        when(exchangeRateService.getExchangeRate(
            ExchangeRateSource.MOCK,
            Currency.UAH,
            Currency.USD
        )).thenReturn(
            rate(
                Currency.USD,
                Currency.UAH,
                "40.00",
                "41.00"
            )
        );

        CurrencyConversionResponseDto response =
            currencyConversionService.convert(request);

        assertEquals(
            0,
            new BigDecimal("24.39024390243902439024390243902439")
                .compareTo(response.steps().getFirst().targetAmount())
        );

        assertEquals(
            new BigDecimal("24.39"),
            response.targetAmount()
        );
    }
}
