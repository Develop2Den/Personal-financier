package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.utils.HtmlSanitizerService;
import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.dto.goalDTO.GoalRequestDto;
import com.d2d.personal_financier.dto.goalDTO.GoalResponseDto;
import com.d2d.personal_financier.entity.Goal;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.entity.enums.GoalStatus;
import com.d2d.personal_financier.exception.GoalNotFoundException;
import com.d2d.personal_financier.mapper.GoalMapper;
import com.d2d.personal_financier.repository.GoalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final SecurityUtils securityUtils;
    private final HtmlSanitizerService sanitizer;

    public GoalResponseDto createGoal(GoalRequestDto dto) {

        Goal goal = goalMapper.toEntity(dto);

        goal.setName(
            sanitizer.sanitize(dto.name())
        );

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

        User user = securityUtils.getCurrentUser();

        Goal goal = goalRepository.findByIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new GoalNotFoundException(id));

        return goalMapper.toDto(goal);
    }

    public GoalResponseDto updateGoal(Long id, GoalRequestDto dto) {

        User user = securityUtils.getCurrentUser();

        Goal goal = goalRepository.findByIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new GoalNotFoundException(id));

        goal.setName(dto.name());
        goal.setTargetAmount(dto.targetAmount());
        goal.setDeadline(dto.deadline());

        goalRepository.save(goal);

        return goalMapper.toDto(goal);
    }

    public void deleteGoal(Long id) {

        User user = securityUtils.getCurrentUser();

        Goal goal = goalRepository.findByIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new GoalNotFoundException(id));

        goalRepository.delete(goal);
    }
}

