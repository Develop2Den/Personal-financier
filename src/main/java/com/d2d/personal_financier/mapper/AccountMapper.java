package com.d2d.personal_financier.mapper;

import com.d2d.personal_financier.dto.account_dto.AccountRequestDto;
import com.d2d.personal_financier.dto.account_dto.AccountResponseDto;
import com.d2d.personal_financier.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "owner", ignore = true)
    Account toEntity(AccountRequestDto dto);

    AccountResponseDto toDto(Account entity);

}

