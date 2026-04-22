package com.d2d.personal_financier.dto.authDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Refresh token request")
public record RefreshTokenRequestDto(

    @NotBlank(message = "Refresh token is required")
    @Schema(description = "Refresh token used to obtain a new token pair")
    String refreshToken

) {}
