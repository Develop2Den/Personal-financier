package com.D2D.personal_financier.mapper;

import com.D2D.personal_financier.dto.userDTO.UserRequestDto;
import com.D2D.personal_financier.dto.userDTO.UserResponseDto;
import com.D2D.personal_financier.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserRequestDto dto);

    UserResponseDto toDto(User entity);
}
