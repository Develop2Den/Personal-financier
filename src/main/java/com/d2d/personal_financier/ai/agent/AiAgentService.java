package com.d2d.personal_financier.ai.agent;

import com.d2d.personal_financier.ai.agent.analytics.model.AgentExecution;
import com.d2d.personal_financier.ai.agent.analytics.model.AiRequest;
import com.d2d.personal_financier.ai.agent.analytics.model.AiResponse;
import com.d2d.personal_financier.ai.agent.analytics.model.ResolvedIntent;
import com.d2d.personal_financier.ai.agent.infrastructure.executor.AiAgent;
import com.d2d.personal_financier.ai.agent.infrastructure.registry.AiAgentRegistry;
import com.d2d.personal_financier.ai.agent.infrastructure.resolver.IntentResolver;
import com.d2d.personal_financier.ai.service.AiInferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiAgentService {

    private final IntentResolver intentResolver;
    private final AiAgentRegistry agentRegistry;
    private final AiInferenceService aiInferenceService;

    public AiResponse execute(AiRequest request) {

        ResolvedIntent resolvedIntent = intentResolver.resolve(request);

        AiAgent agent = agentRegistry.getAgent(resolvedIntent.intent());

        AgentExecution execution = agent.execute(resolvedIntent);

        return aiInferenceService.ask(execution.prompt());
    }

}
