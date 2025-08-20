package com.D2D.personal_financier.service;

import com.D2D.personal_financier.dto.goalDTO.GoalRequestDto;
import com.D2D.personal_financier.dto.goalDTO.GoalResponseDto;
import com.D2D.personal_financier.entity.Goal;
import com.D2D.personal_financier.mapper.GoalMapper;
import com.D2D.personal_financier.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;

    public GoalResponseDto createGoal(GoalRequestDto dto) {
        Goal goal = goalMapper.toEntity(dto);
        goalRepository.save(goal);
        return goalMapper.toDto(goal);
    }

    public List<GoalResponseDto> getAllGoals() {
        return goalRepository.findAll().stream()
                .map(goalMapper::toDto)
                .collect(Collectors.toList());
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
        goalRepository.deleteById(id);
    }
}

