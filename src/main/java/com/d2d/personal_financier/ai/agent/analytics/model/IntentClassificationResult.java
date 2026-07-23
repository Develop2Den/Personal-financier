package com.d2d.personal_financier.ai.agent.analytics.model;

import com.fasterxml.jackson.databind.JsonNode;

public record IntentClassificationResult(

    AiIntent intent,

    String action,

    JsonNode parameters

) {
}
