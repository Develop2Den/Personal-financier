package com.d2d.personal_financier.ai.agent.classification;

import com.d2d.personal_financier.ai.agent.analytics.model.AiRequest;
import org.springframework.stereotype.Service;

@Service
public class IntentPromptBuilderService {

    public String buildPrompt(AiRequest request) {

        return """
            %s

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
                PromptTemplates.JSON_SCHEMA,
                ClassificationExamples.EXAMPLES,
                request.message()
            );
    }

}
