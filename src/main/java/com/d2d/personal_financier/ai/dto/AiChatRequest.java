package com.d2d.personal_financier.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "AI chat request")
public record AiChatRequest(

    @NotBlank(message = "Question must not be blank")
    @Schema(
        description = "Financial question for AI assistant",
        example = "How much did I spend this month?"
    )
    String question

) {
}
