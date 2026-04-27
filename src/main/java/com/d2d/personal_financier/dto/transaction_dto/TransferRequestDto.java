package com.d2d.personal_financier.dto.transaction_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Request for transferring money between accounts")
public record TransferRequestDto(

        @NotNull(message = "Source account id is required")
        @Positive(message = "Source account id must be positive")
        @Schema(description = "Source account ID", example = "1")
        Long fromAccountId,

        @NotNull(message = "Destination account id is required")
        @Positive(message = "Destination account id must be positive")
        @Schema(description = "Destination account ID", example = "2")
        Long toAccountId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0")
        @Schema(description = "Transfer amount", example = "150.00")
        BigDecimal amount,

        @Size(max = 255, message = "Description must not exceed 255 characters")
        @Schema(description = "Transfer description", example = "Card to cash")
        String description,

        @PastOrPresent(message = "Transfer date cannot be in the future")
        @Schema(description = "Transfer date", example = "2026-04-27T11:00:00")
        LocalDateTime date
) {}
