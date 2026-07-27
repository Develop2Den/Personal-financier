package com.d2d.personal_financier.ai.agent.agents.category.model;

import com.d2d.personal_financier.ai.agent.infrastructure.model.AgentParameters;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Parameters for category AI agent")
public record CategoryParameters(

) implements AgentParameters {
}
