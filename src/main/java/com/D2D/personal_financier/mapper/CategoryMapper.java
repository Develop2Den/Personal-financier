package com.D2D.personal_financier.mapper;

import com.D2D.personal_financier.dto.categoryDTO.CategoryRequestDto;
import com.D2D.personal_financier.dto.categoryDTO.CategoryResponseDto;
import com.D2D.personal_financier.entity.Category;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequestDto dto);
    CategoryResponseDto toDto(Category entity);
}

