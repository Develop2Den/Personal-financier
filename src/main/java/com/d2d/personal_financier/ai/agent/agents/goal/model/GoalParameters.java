package com.d2d.personal_financier.ai.agent.agents.goal.model;

import com.d2d.personal_financier.ai.agent.infrastructure.model.AgentParameters;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Parameters for goal AI agent")
public record GoalParameters(

) implements AgentParameters {
}
