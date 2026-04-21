package com.d2d.personal_financier.mapper;

import com.d2d.personal_financier.dto.transactionDTO.TransactionRequestDto;
import com.d2d.personal_financier.dto.transactionDTO.TransactionResponseDto;
import com.d2d.personal_financier.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Transaction toEntity(TransactionRequestDto dto);

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "categoryId", source = "category.id")
    TransactionResponseDto toDto(Transaction entity);
}

