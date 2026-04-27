package com.d2d.personal_financier.dto.transaction_dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Transfer response object")
public record TransferResponseDto(

        @Schema(description = "Transfer reference", example = "6f9e4542-7df0-4c35-b5ce-cfe0b7d9519e")
        String transferReference,

        @Schema(description = "Transfer amount", example = "150.00")
        BigDecimal amount,

        @Schema(description = "Transfer currency", example = "USD")
        String currency,

        @Schema(description = "Transfer description", example = "Card to cash")
        String description,

        @Schema(description = "Transfer date", example = "2026-04-27T11:00:00")
        LocalDateTime date,

        @Schema(description = "Source account ID", example = "1")
        Long fromAccountId,

        @Schema(description = "Destination account ID", example = "2")
        Long toAccountId,

        @Schema(description = "Outgoing transaction ID", example = "101")
        Long outgoingTransactionId,

        @Schema(description = "Incoming transaction ID", example = "102")
        Long incomingTransactionId,

        @Schema(description = "Source account balance after transfer", example = "850.00")
        BigDecimal fromAccountBalance,

        @Schema(description = "Destination account balance after transfer", example = "250.00")
        BigDecimal toAccountBalance
) {}
