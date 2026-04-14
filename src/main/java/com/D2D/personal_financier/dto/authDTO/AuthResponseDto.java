package com.D2D.personal_financier.dto.authDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response containing JWT token")
public record AuthResponseDto(

    @Schema(
        description = "JWT access token used for authenticated requests",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    String token

) {}
