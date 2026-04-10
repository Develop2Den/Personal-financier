package com.D2D.personal_financier.mapper;

import com.D2D.personal_financier.dto.accountDTO.AccountRequestDto;
import com.D2D.personal_financier.dto.accountDTO.AccountResponseDto;
import com.D2D.personal_financier.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "owner", ignore = true)
    Account toEntity(AccountRequestDto dto);

    AccountResponseDto toDto(Account entity);

}

