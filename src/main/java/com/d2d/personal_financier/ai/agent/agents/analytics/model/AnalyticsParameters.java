package com.d2d.personal_financier.ai.agent.agents.analytics.model;

import com.d2d.personal_financier.ai.agent.infrastructure.model.AgentParameters;

public record AnalyticsParameters(

    String period,

    String account,

    String category

) implements AgentParameters {
}
