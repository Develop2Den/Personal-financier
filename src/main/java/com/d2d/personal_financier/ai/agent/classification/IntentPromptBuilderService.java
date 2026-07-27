package com.d2d.personal_financier.ai.agent.classification;

import com.d2d.personal_financier.ai.agent.agents.analytics.model.AiRequest;
import org.springframework.stereotype.Service;

import static com.d2d.personal_financier.ai.agent.classification.ParameterDefinitions.PARAMETERS;

@Service
public class IntentPromptBuilderService {

    public String buildPrompt(AiRequest request) {

        return """
            %s

            Available AI Intents:

            %s

            Available Business Actions:

            %s

            %s

            %s

            %s

            %s

            User request:

            %s
            """
            .formatted(
                PromptTemplates.SYSTEM_RULES,
                IntentDefinitions.AVAILABLE_INTENTS,
                ActionDefinitions.AVAILABLE_ACTIONS,
                PromptTemplates.CLASSIFICATION_RULES,
                PARAMETERS,
                PromptTemplates.JSON_SCHEMA,
                ClassificationExamples.EXAMPLES,
                request.message()
            );
    }

}
