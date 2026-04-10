package com.D2D.personal_financier.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Report of spending grouped by category")
public record CategoryReportDto(

        @Schema(description = "Category name", example = "Food")
        String category,

        @Schema(description = "Total amount spent in this category", example = "450.00")
        BigDecimal amount

) {}
