package com.d2d.personal_financier.dto.currency_dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NbuExchangeRateResponseDto(

    Integer r030,

    String txt,

    BigDecimal rate,

    String cc,

    String exchangedate

) {
}
