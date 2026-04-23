package com.d2d.personal_financier.dto.category_dto;

import com.d2d.personal_financier.entity.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response with category information")
public record CategoryResponseDto(

        @Schema(description = "Category ID", example = "1")
        Long id,

        @Schema(description = "Category name", example = "Food")
        String name,

        @Schema(description = "Transaction type associated with category", example = "EXPENSE")
        TransactionType type

) {}
