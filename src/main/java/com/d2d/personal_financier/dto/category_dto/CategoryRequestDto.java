package com.d2d.personal_financier.dto.category_dto;

import com.d2d.personal_financier.entity.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request for creating a category")
public record CategoryRequestDto(

        @NotBlank(message = "Category name is required")
        @Size(max = 50, message = "Category name must not exceed 50 characters")
        @Schema(description = "Category name", example = "Food")
        String name,

        @NotNull(message = "Category type is required")
        @Schema(description = "Transaction type associated with category", example = "EXPENSE")
        TransactionType type

) {}
