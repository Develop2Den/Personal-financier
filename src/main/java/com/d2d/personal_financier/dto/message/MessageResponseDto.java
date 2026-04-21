package com.d2d.personal_financier.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Generic response containing a human-readable message")
public record MessageResponseDto(

    @Schema(
        description = "Informational response message",
        example = "Registration successful. Please check your email and verify it before logging in."
    )
    String message

) {}
