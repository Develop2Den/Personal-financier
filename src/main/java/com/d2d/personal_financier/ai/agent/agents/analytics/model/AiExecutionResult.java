package com.d2d.personal_financier.ai.agent.agents.analytics.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AI agent execution result")
public record AiExecutionResult(

    @Schema(description = "Data returned by AI agent", example = "{\"totalExpenses\":1200.00}")
    Object data

) {
}
