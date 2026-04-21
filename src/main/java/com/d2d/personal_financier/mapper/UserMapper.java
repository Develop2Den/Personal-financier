package com.d2d.personal_financier.mapper;

import com.d2d.personal_financier.dto.userDTO.UserRequestDto;
import com.d2d.personal_financier.dto.userDTO.UserResponseDto;
import com.d2d.personal_financier.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // пароль кодируется в сервисе
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserRequestDto dto);

    UserResponseDto toDto(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateUserFromDto(UserRequestDto dto, @MappingTarget User user);

}


