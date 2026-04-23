package com.d2d.personal_financier.mapper;

import com.d2d.personal_financier.dto.budget_dto.BudgetRequestDto;
import com.d2d.personal_financier.dto.budget_dto.BudgetResponseDto;
import com.d2d.personal_financier.entity.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "category", ignore = true)
    Budget toEntity(BudgetRequestDto dto);

    @Mapping(target = "categoryId", source = "category.id")
    BudgetResponseDto toDto(Budget entity);

}

