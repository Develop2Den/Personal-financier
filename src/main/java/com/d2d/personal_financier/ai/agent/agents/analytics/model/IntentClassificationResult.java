package com.d2d.personal_financier.ai.agent.agents.analytics.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AI intent classification result")
public record IntentClassificationResult(

    @Schema(description = "Classified AI intent", example = "ANALYTICS")
    AiIntent intent,

    @Schema(description = "Classified AI action", example = "SUMMARY")
    String action,

    @Schema(description = "Parameters extracted from AI request", example = "{\"period\":\"2026-04\"}")
    JsonNode parameters

) {
}
