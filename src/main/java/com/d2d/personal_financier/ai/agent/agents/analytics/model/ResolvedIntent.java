package com.d2d.personal_financier.ai.agent.agents.analytics.model;

import com.d2d.personal_financier.ai.agent.infrastructure.model.AgentParameters;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resolved AI intent")
public record ResolvedIntent(

    @Schema(description = "Classified AI intent", example = "ANALYTICS")
    AiIntent intent,

    @Schema(description = "Classified AI action", example = "SUMMARY")
    String action,

    @Schema(description = "Parameters resolved for AI agent", example = "{\"period\":\"2026-04\"}")
    AgentParameters parameters,

    @Schema(description = "User message", example = "Show my monthly expenses.")
    String userMessage

) {
}
