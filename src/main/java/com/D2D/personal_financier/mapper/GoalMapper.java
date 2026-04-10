package com.D2D.personal_financier.mapper;

import com.D2D.personal_financier.dto.goalDTO.GoalRequestDto;
import com.D2D.personal_financier.dto.goalDTO.GoalResponseDto;
import com.D2D.personal_financier.entity.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "currentAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    Goal toEntity(GoalRequestDto dto);

    @Mapping(target = "progress", expression = "java(calculateProgress(entity))")
    GoalResponseDto toDto(Goal entity);

    default Integer calculateProgress(Goal goal) {

        if (goal.getTargetAmount() == null || goal.getTargetAmount().compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }

        return goal.getCurrentAmount()
                .multiply(BigDecimal.valueOf(100))
                .divide(goal.getTargetAmount(), 0, RoundingMode.HALF_UP)
                .intValue();
    }
}

