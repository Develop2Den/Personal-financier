package com.d2d.personal_financier.dto.currency_dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NbuExchangeRateResponseDto(

    Integer r030,

    String txt,

    BigDecimal rate,

    String cc,

    @JsonFormat(pattern = "dd.MM.yyyy")
    LocalDate exchangedate

) {
}
