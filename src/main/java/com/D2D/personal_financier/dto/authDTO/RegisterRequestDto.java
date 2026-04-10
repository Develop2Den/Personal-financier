package com.D2D.personal_financier.dto.authDTO;

public record RegisterRequestDto(
        String username,
        String email,
        String password
) {}
