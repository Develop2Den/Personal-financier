package com.d2d.personal_financier.ai.agent.agents.analytics.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AI request")
public record AiRequest(

    @Schema(description = "Message sent to AI agent", example = "Show my monthly expenses.")
    String message

) {
}
