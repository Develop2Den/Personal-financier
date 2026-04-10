package com.D2D.personal_financier.mapper;

import com.D2D.personal_financier.dto.budgetDTO.BudgetRequestDto;
import com.D2D.personal_financier.dto.budgetDTO.BudgetResponseDto;
import com.D2D.personal_financier.entity.Budget;
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

