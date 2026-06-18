package com.d2d.personal_financier.mapper;

import com.d2d.personal_financier.dto.account_dto.AccountRequestDto;
import com.d2d.personal_financier.dto.account_dto.AccountResponseDto;
import com.d2d.personal_financier.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "active", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "version", ignore = true)
    Account toEntity(AccountRequestDto dto);

    AccountResponseDto toDto(Account entity);

}

