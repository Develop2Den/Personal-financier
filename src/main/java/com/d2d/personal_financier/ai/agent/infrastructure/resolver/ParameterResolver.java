package com.d2d.personal_financier.ai.agent.infrastructure.resolver;

import com.d2d.personal_financier.ai.agent.agents.account.model.AccountParameters;
import com.d2d.personal_financier.ai.agent.agents.analytics.model.AiIntent;
import com.d2d.personal_financier.ai.agent.agents.analytics.model.AnalyticsParameters;
import com.d2d.personal_financier.ai.agent.agents.budget.model.BudgetParameters;
import com.d2d.personal_financier.ai.agent.agents.category.model.CategoryParameters;
import com.d2d.personal_financier.ai.agent.agents.currency.model.CurrencyParameters;
import com.d2d.personal_financier.ai.agent.agents.goal.model.GoalParameters;
import com.d2d.personal_financier.ai.agent.agents.transaction.model.TransactionParameters;
import com.d2d.personal_financier.ai.agent.infrastructure.model.AgentParameters;
import com.d2d.personal_financier.ai.parser.AiJsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParameterResolver {

    private final AiJsonParser aiJsonParser;

    public AgentParameters resolve(
        AiIntent intent,
        JsonNode parameters) {

        return switch (intent) {

            case ANALYTICS -> aiJsonParser.convert(
                parameters,
                AnalyticsParameters.class
            );
            case ACCOUNT -> aiJsonParser.convert(
                parameters,
                AccountParameters.class
            );
            case TRANSACTION -> aiJsonParser.convert(
                parameters,
                TransactionParameters.class
            );
            case GOAL -> aiJsonParser.convert(
                parameters,
                GoalParameters.class
            );
            case BUDGET -> aiJsonParser.convert(
                parameters,
                BudgetParameters.class
            );
            case CATEGORY -> aiJsonParser.convert(
                parameters,
                CategoryParameters.class
            );
            case CURRENCY -> aiJsonParser.convert(
                parameters,
                CurrencyParameters.class
            );

            case SYSTEM -> throw new UnsupportedOperationException(
                "Parameters are not supported for intent: " + intent
            );
        };
    }
}
