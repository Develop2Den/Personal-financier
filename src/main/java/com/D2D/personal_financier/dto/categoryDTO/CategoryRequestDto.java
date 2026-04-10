package com.D2D.personal_financier.dto.categoryDTO;

import com.D2D.personal_financier.entity.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request for creating a category")
public record CategoryRequestDto(

        @Schema(description = "Category name", example = "Food")
        String name,

        @Schema(description = "Transaction type associated with category", example = "EXPENSE")
        TransactionType type

) {}
