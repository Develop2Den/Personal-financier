package com.D2D.personal_financier.mapper;

import com.D2D.personal_financier.dto.userDTO.UserRequestDto;
import com.D2D.personal_financier.dto.userDTO.UserResponseDto;
import com.D2D.personal_financier.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDto dto);
    UserResponseDto toDto(User entity);
}
