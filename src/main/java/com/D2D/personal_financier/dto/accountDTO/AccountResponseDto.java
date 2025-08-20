package com.D2D.personal_financier.dto.accountDTO;

public record AccountResponseDto(
        Long id,
        String name,
        String currency,
        Double balance
) {}
