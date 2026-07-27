package com.d2d.personal_financier.ai.agent.agents.analytics.model;

import com.d2d.personal_financier.ai.agent.infrastructure.model.AgentParameters;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Parameters for analytics AI agent")
public record AnalyticsParameters(

    @Schema(description = "Analytics period", example = "2026-04")
    String period,

    @Schema(description = "Account name", example = "Main Card")
    String account,

    @Schema(description = "Category name", example = "Food")
    String category

) implements AgentParameters {
}
