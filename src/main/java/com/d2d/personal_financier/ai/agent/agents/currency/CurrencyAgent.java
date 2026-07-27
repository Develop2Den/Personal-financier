package com.d2d.personal_financier.ai.agent.agents.currency;

import com.d2d.personal_financier.ai.agent.agents.analytics.model.AgentExecution;
import com.d2d.personal_financier.ai.agent.agents.analytics.model.AiIntent;
import com.d2d.personal_financier.ai.agent.agents.analytics.model.ResolvedIntent;
import com.d2d.personal_financier.ai.agent.agents.currency.model.CurrencyContextDto;
import com.d2d.personal_financier.ai.agent.agents.currency.model.CurrencyParameters;
import com.d2d.personal_financier.ai.agent.agents.currency.prompt.CurrencyPromptBuilderService;
import com.d2d.personal_financier.ai.agent.infrastructure.executor.AiAgent;
import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import com.d2d.personal_financier.ai.service.FinancialContextService;
import com.d2d.personal_financier.dto.currency_dto.ExchangeRateDto;
import com.d2d.personal_financier.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CurrencyAgent implements AiAgent {

    private static final Logger log =
        LoggerFactory.getLogger(CurrencyAgent.class);

    private final FinancialContextService financialContextService;
    private final ExchangeRateService exchangeRateService;
    private final CurrencyPromptBuilderService currencyPromptBuilderService;

    @Override
    public AiIntent supportedIntent() {
        return AiIntent.CURRENCY;
    }

    @Override
    public AgentExecution execute(
        ResolvedIntent resolvedIntent
    ) {

        log.info(
            "CurrencyAgent executed. Action: {}",
            resolvedIntent.action()
        );

        FinancialContextDto financialContext =
            financialContextService.buildContext();

        CurrencyParameters parameters =
            (CurrencyParameters) resolvedIntent.parameters();

        log.info(
            "Currency parameters: from={}, to={}, source={}",
            parameters.fromCurrency(),
            parameters.toCurrency(),
            parameters.source()
        );

        List<ExchangeRateDto> exchangeRates =
            getExchangeRates(parameters);

        CurrencyContextDto currencyContext =
            new CurrencyContextDto(
                financialContext,
                parameters,
                exchangeRates
            );

        String prompt =
            currencyPromptBuilderService.buildPrompt(
                currencyContext
            );

        return new AgentExecution(prompt);
    }

    private List<ExchangeRateDto> getExchangeRates(
        CurrencyParameters parameters
    ) {

        if (parameters.source() != null) {
            return exchangeRateService.getExchangeRates(
                parameters.source()
            );
        }

        return exchangeRateService.getAllExchangeRates();
    }
}
