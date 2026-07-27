package com.d2d.personal_financier.ai.dto;

import com.d2d.personal_financier.entity.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Category summary used by AI assistant")
public record CategorySummaryDto(
    @Schema(description = "Category name", example = "Food")
    String name,

    @Schema(description = "Category type", example = "EXPENSE")
    TransactionType type,

    @Schema(description = "Category active status", example = "true")
    Boolean active
) {
}
