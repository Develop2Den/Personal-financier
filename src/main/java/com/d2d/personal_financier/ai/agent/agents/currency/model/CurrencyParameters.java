package com.d2d.personal_financier.ai.agent.agents.currency.model;

import com.d2d.personal_financier.ai.agent.infrastructure.model.AgentParameters;
import com.d2d.personal_financier.entity.enums.Currency;
import com.d2d.personal_financier.entity.enums.ExchangeRateSource;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Currency agent parameters")
public record CurrencyParameters(

    @Schema(
        description = "Source currency",
        example = "USD"
    )
    Currency fromCurrency,

    @Schema(
        description = "Target currency",
        example = "UAH"
    )
    Currency toCurrency,

    @Schema(
        description = "Exchange rate provider",
        example = "MONOBANK"
    )
    ExchangeRateSource source

) implements AgentParameters {
}
