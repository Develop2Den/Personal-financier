package com.D2D.personal_financier.dto.userDTO;

import com.D2D.personal_financier.entity.enums.Role;

import java.time.LocalDateTime;

public record UserResponseDto (
        Long id,
        String username,
        String email,
        Boolean verified,
        Boolean active,
        Role role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
