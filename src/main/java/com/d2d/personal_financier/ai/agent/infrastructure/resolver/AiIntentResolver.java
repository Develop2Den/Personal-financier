package com.d2d.personal_financier.ai.agent.infrastructure.resolver;

import com.d2d.personal_financier.ai.agent.agents.analytics.model.AiRequest;
import com.d2d.personal_financier.ai.agent.agents.analytics.model.ResolvedIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiIntentResolver implements IntentResolver {

    private final IntentClassificationService intentClassificationService;

    @Override
    public ResolvedIntent resolve(AiRequest request) {

        return intentClassificationService.classify(request);

    }
}
