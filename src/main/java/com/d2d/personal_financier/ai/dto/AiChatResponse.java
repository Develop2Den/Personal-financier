package com.d2d.personal_financier.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AI chat response")
public record AiChatResponse(

    @Schema(
        description = "AI generated answer",
        example = "Your total expenses this month were 1200.00 UAH."
    )
    String answer

) {
}
