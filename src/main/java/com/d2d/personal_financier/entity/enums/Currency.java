package com.d2d.personal_financier.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {

    UAH("UAH", "₴", "Украинская гривна"),
    USD("USD", "$", "US Dollar"),
    EUR("EUR", "€", "Euro");

    private final String code;
    private final String symbol;
    private final String description;
}
