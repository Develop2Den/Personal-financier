package com.d2d.personal_financier.dto.token_dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Email verification token")
public record EmailVerificationDto(

        @Schema(
                description = "Verification token sent to user's email",
                example = "c2c87a60-5b91-4c7c-9c21-8a9a2d1c9f10"
        )
        String token

) {}

