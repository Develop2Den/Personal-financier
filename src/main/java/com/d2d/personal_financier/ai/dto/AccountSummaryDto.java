package com.d2d.personal_financier.ai.dto;

import com.d2d.personal_financier.entity.enums.AccountType;

import java.math.BigDecimal;

public record AccountSummaryDto(

    String name,

    AccountType type,

    BigDecimal balance,

    String currency

) {
}
