package com.d2d.personal_financier.mapper;

import com.d2d.personal_financier.dto.categoryDTO.CategoryRequestDto;
import com.d2d.personal_financier.dto.categoryDTO.CategoryResponseDto;
import com.d2d.personal_financier.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "owner", ignore = true)
    Category toEntity(CategoryRequestDto dto);

    CategoryResponseDto toDto(Category entity);

}

