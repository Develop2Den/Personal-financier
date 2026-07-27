package com.d2d.personal_financier.ai.agent.agents.transaction.model;

import com.d2d.personal_financier.ai.agent.infrastructure.model.AgentParameters;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Parameters for transaction AI agent")
public record TransactionParameters(

) implements AgentParameters {
}
