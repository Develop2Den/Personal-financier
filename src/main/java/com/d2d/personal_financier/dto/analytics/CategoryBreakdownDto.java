package com.d2d.personal_financier.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Category breakdown for a selected month")
public record CategoryBreakdownDto(

    @Schema(description = "Category name", example = "Food")
    String category,

    @Schema(description = "Total amount for the category", example = "450.00")
    BigDecimal amount,

    @Schema(description = "Share of the category in total monthly expenses", example = "37.50")
    BigDecimal percentage,

    @Schema(description = "Number of transactions in the category", example = "4")
    Long transactionCount

) {}
