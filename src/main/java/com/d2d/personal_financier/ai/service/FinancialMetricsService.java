package com.d2d.personal_financier.ai.service;

import com.d2d.personal_financier.ai.dto.FinancialMetricsDto;
import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.entity.Goal;
import com.d2d.personal_financier.entity.Transaction;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.entity.enums.GoalStatus;
import com.d2d.personal_financier.entity.enums.TransactionType;
import com.d2d.personal_financier.repository.GoalRepository;
import com.d2d.personal_financier.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class FinancialMetricsService {

    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final SecurityUtils securityUtils;

    public FinancialMetricsDto buildMetrics() {

        User user = securityUtils.getCurrentUser();

        Goal nearestGoal = findNearestGoal(user.getId());

        return new FinancialMetricsDto(
            nearestGoal != null ? nearestGoal.getName() : null,
            calculateRemainingGoalAmount(nearestGoal),
            calculateGoalCompletionPercentage(nearestGoal),
            findLargestExpense(user.getId()),
            null,
            null,
            null,
            null
        );
    }

    private Goal findNearestGoal(Long userId) {

        return goalRepository.findByOwnerId(userId)
            .stream()
            .filter(goal -> goal.getStatus() == GoalStatus.ACTIVE)
            .filter(goal -> goal.getDeadline() != null)
            .min(Comparator.comparing(Goal::getDeadline))
            .orElse(null);
    }

    private BigDecimal calculateRemainingGoalAmount(Goal goal) {

        if (goal == null) {
            return BigDecimal.ZERO;
        }

        return goal.getTargetAmount()
            .subtract(goal.getCurrentAmount());
    }

    private BigDecimal calculateGoalCompletionPercentage(Goal goal) {

        if (goal == null) {
            return BigDecimal.ZERO;
        }

        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return goal.getCurrentAmount()
            .multiply(BigDecimal.valueOf(100))
            .divide(
                goal.getTargetAmount(),
                2,
                RoundingMode.HALF_UP
            );
    }

    private BigDecimal findLargestExpense(Long userId) {

        return transactionRepository.findByOwnerIdAndType(
                userId,
                TransactionType.EXPENSE
            )
            .stream()
            .map(Transaction::getAmount)
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
    }
}
