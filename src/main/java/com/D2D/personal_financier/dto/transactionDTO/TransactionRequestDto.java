package com.D2D.personal_financier.dto.transactionDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.D2D.personal_financier.entity.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request for creating a transaction")
public record TransactionRequestDto(

        @Schema(description = "Transaction amount", example = "250.50")
        BigDecimal amount,

        @Schema(description = "Transaction type", example = "EXPENSE")
        TransactionType type,

        @Schema(description = "Transaction description", example = "Dinner at restaurant")
        String description,

        @Schema(description = "Transaction date", example = "2026-04-10T18:30:00")
        LocalDateTime date,

        @Schema(description = "Account ID associated with transaction", example = "1")
        Long accountId,

        @Schema(description = "Category ID associated with transaction", example = "3")
        Long categoryId
) {}
