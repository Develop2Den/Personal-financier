package com.d2d.personal_financier.dto.authDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.d2d.personal_financier.validation.ValidPassword;

@Schema(description = "User registration request")
public record RegisterRequestDto(

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(
        regexp = "^[A-Za-z0-9._-]+$",
        message = "Username may contain only letters, numbers, dots, underscores and hyphens"
    )
    @Schema(description = "Unique username", example = "denisdev")
    String username,

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(description = "User email address", example = "denis@example.com")
    String email,

    @NotBlank(message = "Password is required")
    @ValidPassword
    @Schema(
        description = "Secure password (min 8 chars, upper/lowercase, number, special char)",
        example = "MyPass123!"
    )
    String password

) {}
