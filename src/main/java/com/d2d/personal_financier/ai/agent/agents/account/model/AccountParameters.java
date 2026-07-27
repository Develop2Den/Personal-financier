package com.d2d.personal_financier.ai.agent.agents.account.model;

import com.d2d.personal_financier.ai.agent.infrastructure.model.AgentParameters;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Parameters for account AI agent")
public record AccountParameters(

) implements AgentParameters {
}
