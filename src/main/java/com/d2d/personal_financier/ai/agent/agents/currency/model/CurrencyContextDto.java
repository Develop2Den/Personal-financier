package com.d2d.personal_financier.ai.agent.agents.currency.model;

import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import com.d2d.personal_financier.dto.currency_dto.ExchangeRateDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Currency agent context")
public record CurrencyContextDto(

    @Schema(description = "User financial context")
    FinancialContextDto financialContext,

    @Schema(description = "Currency request parameters")
    CurrencyParameters parameters,

    @Schema(description = "Available exchange rates")
    List<ExchangeRateDto> exchangeRates

) {
}
