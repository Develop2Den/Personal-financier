package com.d2d.personal_financier.ai.agent.infrastructure.resolver;

import com.d2d.personal_financier.ai.agent.analytics.model.AiIntent;
import com.d2d.personal_financier.ai.agent.analytics.model.AnalyticsParameters;
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

            case ACCOUNT,
                 TRANSACTION,
                 CATEGORY,
                 GOAL,
                 BUDGET,
                 CURRENCY,
                 DASHBOARD,
                 SYSTEM -> throw new UnsupportedOperationException(
                "Parameters are not supported for intent: " + intent
            );
        };
    }
}
