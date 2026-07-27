package com.d2d.personal_financier.ai.agent.agents.analytics.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AI response")
public record AiResponse(
    @Schema(description = "Content returned by AI provider", example = "Your total expenses this month were 1200.00 UAH.")
    String content
) {
}
