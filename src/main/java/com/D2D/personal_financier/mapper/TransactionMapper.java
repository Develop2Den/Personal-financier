package com.D2D.personal_financier.mapper;

import com.D2D.personal_financier.dto.transactionDTO.TransactionRequestDto;
import com.D2D.personal_financier.dto.transactionDTO.TransactionResponseDto;
import com.D2D.personal_financier.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    Transaction toEntity(TransactionRequestDto dto);
    TransactionResponseDto toDto(Transaction entity);
}

