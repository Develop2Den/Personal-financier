package com.d2d.personal_financier.ai.agent.agents.analytics.model;

import com.d2d.personal_financier.ai.agent.infrastructure.model.AgentParameters;

public record ResolvedIntent(

    AiIntent intent,

    String action,

    AgentParameters parameters,

    String userMessage

) {
}
