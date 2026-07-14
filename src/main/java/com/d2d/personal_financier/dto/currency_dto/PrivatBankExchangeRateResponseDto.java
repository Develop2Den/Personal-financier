package com.d2d.personal_financier.dto.currency_dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PrivatBankExchangeRateResponseDto(

    String ccy,

    String base_ccy,

    BigDecimal buy,

    BigDecimal sale

) {
}
