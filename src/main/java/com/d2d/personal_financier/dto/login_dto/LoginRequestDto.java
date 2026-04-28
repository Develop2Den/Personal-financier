package com.d2d.personal_financier.dto.login_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User login request")
public record LoginRequestDto(

    @NotBlank
    @Size(min = 3, max = 50)
    @Schema(description = "Username", example = "denisdev")
    String username,

    @NotBlank
    @Size(max = 255)
    @Schema(description = "User password", example = "MyPass123!")
    String password

) {}
