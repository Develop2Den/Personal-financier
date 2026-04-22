package com.d2d.personal_financier.dto.authDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response containing an access token and refresh token")
public record AuthResponseDto(

    @Schema(
        description = "JWT access token used for authenticated requests",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    String token,

    @Schema(
        description = "Opaque refresh token used to rotate and obtain a new access token",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    )
    String refreshToken

) {}
