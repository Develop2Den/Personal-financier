package com.D2D.personal_financier.dto.tokenDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request for resending email verification")
public record ResendVerificationRequestDto(

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(description = "User email address", example = "denis@example.com")
    String email

) {}
