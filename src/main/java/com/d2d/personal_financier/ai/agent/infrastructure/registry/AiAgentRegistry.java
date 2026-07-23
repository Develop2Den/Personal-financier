package com.d2d.personal_financier.ai.agent.infrastructure.registry;

import com.d2d.personal_financier.ai.agent.analytics.model.AiIntent;
import com.d2d.personal_financier.ai.agent.infrastructure.executor.AiAgent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AiAgentRegistry {

    private final Map<AiIntent, AiAgent> agents;

    public AiAgentRegistry(List<AiAgent> agents) {
        this.agents = agents.stream()
            .collect(Collectors.toMap(
                AiAgent::supportedIntent,
                Function.identity()
            ));
    }

    public AiAgent getAgent(AiIntent intent) {

        return Optional.ofNullable(agents.get(intent))
            .orElseThrow(() -> new IllegalArgumentException(
                "No AI agent registered for intent: " + intent
            ));
    }
}
