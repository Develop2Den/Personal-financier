package com.D2D.personal_financier.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Standard API error response")
public record ErrorResponse(

    @Schema(description = "HTTP status code", example = "404")
    int status,

    @Schema(description = "Error message", example = "User not found")
    String message,

    @Schema(description = "API path", example = "/api/users/1")
    String path,

    @Schema(description = "Timestamp", example = "2026-04-10T15:30:00")
    LocalDateTime timestamp

) {}
