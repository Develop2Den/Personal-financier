package com.d2d.personal_financier.dto.transactionDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.d2d.personal_financier.entity.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PastOrPresent;

@Schema(description = "Request for creating a transaction")
public record TransactionRequestDto(

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0")
        @Schema(description = "Transaction amount", example = "250.50")
        BigDecimal amount,

        @NotNull(message = "Transaction type is required")
        @Schema(description = "Transaction type", example = "EXPENSE")
        TransactionType type,

        @Size(max = 255, message = "Description must not exceed 255 characters")
        @Schema(description = "Transaction description", example = "Dinner at restaurant")
        String description,

        @PastOrPresent(message = "Transaction date cannot be in the future")
        @Schema(description = "Transaction date", example = "2026-04-10T18:30:00")
        LocalDateTime date,

        @NotNull(message = "Account id is required")
        @Positive(message = "Account id must be positive")
        @Schema(description = "Account ID associated with transaction", example = "1")
        Long accountId,

        @NotNull(message = "Category id is required")
        @Positive(message = "Category id must be positive")
        @Schema(description = "Category ID associated with transaction", example = "3")
        Long categoryId
) {}
