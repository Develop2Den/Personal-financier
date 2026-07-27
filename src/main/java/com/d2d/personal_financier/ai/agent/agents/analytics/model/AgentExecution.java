package com.d2d.personal_financier.ai.agent.agents.analytics.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AI agent execution")
public record AgentExecution(

    @Schema(description = "Prompt sent to AI provider", example = "Analyze the user's monthly expenses for 2026-04.")
    String prompt

) {
}
