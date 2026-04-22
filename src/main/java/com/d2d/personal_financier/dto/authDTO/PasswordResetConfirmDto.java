package com.d2d.personal_financier.dto.authDTO;

import com.d2d.personal_financier.validation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Password reset confirmation request")
public record PasswordResetConfirmDto(

    @NotBlank(message = "Reset token is required")
    @Schema(description = "Password reset token sent by email")
    String token,

    @ValidPassword
    @Schema(description = "New password matching the configured password policy", example = "MyPass123!")
    String newPassword

) {}
