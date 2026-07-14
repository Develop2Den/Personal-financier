package com.d2d.personal_financier.dto.currency_dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MonobankExchangeRateResponseDto(

    Integer currencyCodeA,

    Integer currencyCodeB,

    Long date,

    BigDecimal rateBuy,

    BigDecimal rateSell,

    BigDecimal rateCross

) {
}
