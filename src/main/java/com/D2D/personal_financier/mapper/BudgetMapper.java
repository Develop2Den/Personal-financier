package com.D2D.personal_financier.mapper;

import com.D2D.personal_financier.dto.budgetDTO.BudgetRequestDto;
import com.D2D.personal_financier.dto.budgetDTO.BudgetResponseDto;
import com.D2D.personal_financier.entity.Budget;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface BudgetMapper {

    Budget toEntity(BudgetRequestDto dto);
    BudgetResponseDto toDto(Budget entity);
}

