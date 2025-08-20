package com.D2D.personal_financier.mapper;

import com.D2D.personal_financier.dto.accountDTO.AccountRequestDto;
import com.D2D.personal_financier.dto.accountDTO.AccountResponseDto;
import com.D2D.personal_financier.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    Account toEntity(AccountRequestDto dto);
    AccountResponseDto toDto(Account entity);
}

