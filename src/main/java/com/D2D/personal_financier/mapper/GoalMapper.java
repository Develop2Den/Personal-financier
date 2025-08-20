package com.D2D.personal_financier.mapper;

import com.D2D.personal_financier.dto.goalDTO.GoalRequestDto;
import com.D2D.personal_financier.dto.goalDTO.GoalResponseDto;
import com.D2D.personal_financier.entity.Goal;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface GoalMapper {

    Goal toEntity(GoalRequestDto dto);
    GoalResponseDto toDto(Goal entity);
}

