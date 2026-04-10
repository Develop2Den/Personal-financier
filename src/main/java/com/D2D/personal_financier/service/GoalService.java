package com.D2D.personal_financier.service;

import com.D2D.personal_financier.config.security.SecurityUtils;
import com.D2D.personal_financier.dto.goalDTO.GoalRequestDto;
import com.D2D.personal_financier.dto.goalDTO.GoalResponseDto;
import com.D2D.personal_financier.entity.Goal;
import com.D2D.personal_financier.entity.User;
import com.D2D.personal_financier.entity.enums.GoalStatus;
import com.D2D.personal_financier.mapper.GoalMapper;
import com.D2D.personal_financier.repository.GoalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final SecurityUtils securityUtils;

    public GoalResponseDto createGoal(GoalRequestDto dto) {

        Goal goal = goalMapper.toEntity(dto);

        User user = securityUtils.getCurrentUser();

        goal.setOwner(user);
        goal.setCurrentAmount(BigDecimal.ZERO);
        goal.setStatus(GoalStatus.ACTIVE);

        goalRepository.save(goal);

        return goalMapper.toDto(goal);
    }

    public List<GoalResponseDto> getAllGoals() {

        User user = securityUtils.getCurrentUser();

        return goalRepository.findByOwnerId(user.getId())
                .stream()
                .map(goalMapper::toDto)
                .toList();
    }

    public GoalResponseDto getGoalById(Long id) {

        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        return goalMapper.toDto(goal);
    }

    public GoalResponseDto updateGoal(Long id, GoalRequestDto dto) {

        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        goal.setName(dto.name());
        goal.setTargetAmount(dto.targetAmount());
        goal.setDeadline(dto.deadline());

        goalRepository.save(goal);

        return goalMapper.toDto(goal);
    }

    public void deleteGoal(Long id) {

        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        goalRepository.delete(goal);
    }
}

