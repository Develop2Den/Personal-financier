package com.D2D.personal_financier.dto.accountDTO;

public record AccountRequestDto(
        String name,
        Double balance,
        String currency
) {}
