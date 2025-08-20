package com.D2D.personal_financier.dto.transactionDTO;

import java.time.LocalDateTime;

public record TransactionRequestDto(
        Double amount,
        String type,
        String description,
        LocalDateTime date,
        Long accountId,
        Long categoryId
) {}
