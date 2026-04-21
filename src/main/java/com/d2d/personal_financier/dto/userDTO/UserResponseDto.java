package com.d2d.personal_financier.dto.userDTO;

import com.d2d.personal_financier.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Response containing user information")
public record UserResponseDto(

    @Schema(description = "User ID", example = "1")
    Long id,

    @Schema(description = "Username", example = "denisdev")
    String username,

    @Schema(description = "User email", example = "denis@example.com")
    String email,

    @Schema(description = "Email verification status", example = "true")
    Boolean verified,

    @Schema(description = "Account active status", example = "true")
    Boolean active,

    @Schema(description = "User role", example = "USER")
    Role role,

    @Schema(description = "User creation timestamp", example = "2026-04-10T14:30:00")
    LocalDateTime createdAt,

    @Schema(description = "User last update timestamp", example = "2026-04-10T14:35:00")
    LocalDateTime updatedAt

) {}
