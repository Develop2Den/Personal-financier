package com.d2d.personal_financier.ai.agent.infrastructure.resolver;

import com.d2d.personal_financier.ai.agent.agents.analytics.model.AiRequest;
import com.d2d.personal_financier.ai.agent.agents.analytics.model.AiResponse;
import com.d2d.personal_financier.ai.agent.agents.analytics.model.IntentClassificationResult;
import com.d2d.personal_financier.ai.agent.agents.analytics.model.ResolvedIntent;
import com.d2d.personal_financier.ai.agent.classification.IntentPromptBuilderService;
import com.d2d.personal_financier.ai.agent.infrastructure.model.AgentParameters;
import com.d2d.personal_financier.ai.parser.AiJsonParser;
import com.d2d.personal_financier.ai.service.AiInferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntentClassificationService {

    private final IntentPromptBuilderService intentPromptBuilderService;
    private final AiInferenceService aiInferenceService;
    private final AiJsonParser aiJsonParser;
    private final ParameterResolver parameterResolver;

    public ResolvedIntent classify(AiRequest request) {

        String prompt =
            intentPromptBuilderService.buildPrompt(request);

        AiResponse response =
            aiInferenceService.ask(prompt);

        IntentClassificationResult result =
            aiJsonParser.parse(
                response,
                IntentClassificationResult.class
            );

        log.info("Intent classification: {}", result);

        AgentParameters parameters =
            parameterResolver.resolve(
                result.intent(),
                result.parameters()
            );

        return new ResolvedIntent(
            result.intent(),
            result.action(),
            parameters,
            request.message()
        );
    }
}
