package com.d2d.personal_financier.ai.service;

import com.d2d.personal_financier.ai.dto.FinancialMetricsDto;
import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.entity.Goal;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.entity.enums.GoalStatus;
import com.d2d.personal_financier.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class FinancialMetricsService {

    private final GoalRepository goalRepository;
    private final SecurityUtils securityUtils;

    public FinancialMetricsDto buildMetrics() {

        User user = securityUtils.getCurrentUser();

        Goal nearestGoal = goalRepository.findByOwnerId(user.getId())
            .stream()
            .filter(goal -> goal.getStatus() == GoalStatus.ACTIVE)
            .filter(goal -> goal.getDeadline() != null)
            .min(Comparator.comparing(Goal::getDeadline))
            .orElse(null);

        if (nearestGoal == null) {
            return new FinancialMetricsDto(
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null,
                null,
                null,
                null,
                null
            );
        }

        BigDecimal remainingGoalAmount = nearestGoal.getTargetAmount()
            .subtract(nearestGoal.getCurrentAmount());

        BigDecimal goalCompletionPercentage = BigDecimal.ZERO;

        if (nearestGoal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            goalCompletionPercentage = nearestGoal.getCurrentAmount()
                .multiply(BigDecimal.valueOf(100))
                .divide(
                    nearestGoal.getTargetAmount(),
                    2,
                    RoundingMode.HALF_UP
                );
        }

        return new FinancialMetricsDto(
            nearestGoal.getName(),
            remainingGoalAmount,
            goalCompletionPercentage,
            null,
            null,
            null,
            null,
            null
        );
    }
}
