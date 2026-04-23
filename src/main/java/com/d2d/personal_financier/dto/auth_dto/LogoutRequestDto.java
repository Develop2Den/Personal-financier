package com.d2d.personal_financier.dto.auth_dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Logout request payload")
public record LogoutRequestDto(

    @Schema(description = "Refresh token to revoke during logout")
    String refreshToken

) {}
