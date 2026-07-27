package com.d2d.personal_financier.ai.agent.agents.currency.prompt;

import com.d2d.personal_financier.ai.agent.agents.currency.model.CurrencyContextDto;
import com.d2d.personal_financier.ai.agent.agents.currency.model.CurrencyParameters;
import com.d2d.personal_financier.ai.agent.infrastructure.prompt.AgentPromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CurrencyPromptBuilderService {

    private static final String SYSTEM_INSTRUCTIONS = """
        You are a Currency AI Agent.

        Your responsibility is to answer ONLY currency-related questions.

        Use ONLY the exchange rate information provided.

        Never invent exchange rates.
        Never invent bank information.
        Never answer questions unrelated to currencies.
        Never use knowledge outside the provided context.
        """;

    private final AgentPromptBuilder agentPromptBuilder;

    public String buildPrompt(
        CurrencyContextDto context
    ) {

        return agentPromptBuilder.buildPrompt(
            buildCurrencyRequest(context),
            SYSTEM_INSTRUCTIONS,
            context.financialContext()
        );
    }

    private String buildCurrencyRequest(
        CurrencyContextDto context
    ) {

        CurrencyParameters parameters = context.parameters();

        return """
            User request:

            From currency: %s
            To currency: %s
            Exchange rate source: %s

            Available exchange rates:

            %s
            """
            .formatted(
                parameters.fromCurrency(),
                parameters.toCurrency(),
                parameters.source(),
                context.exchangeRates()
            );
    }

}
