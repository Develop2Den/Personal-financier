package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.dto.budget_dto.BudgetRequestDto;
import com.d2d.personal_financier.dto.budget_dto.BudgetResponseDto;
import com.d2d.personal_financier.entity.Budget;
import com.d2d.personal_financier.entity.Category;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.exception.BudgetNotFoundException;
import com.d2d.personal_financier.exception.CategoryNotFoundException;
import com.d2d.personal_financier.mapper.BudgetMapper;
import com.d2d.personal_financier.repository.BudgetRepository;
import com.d2d.personal_financier.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetMapper budgetMapper;
    private final SecurityUtils securityUtils;

    public BudgetResponseDto createBudget(BudgetRequestDto dto) {

        User user = securityUtils.getCurrentUser();

        Category category = categoryRepository.findByIdAndOwnerId(dto.categoryId(), user.getId())
                .orElseThrow(() -> new CategoryNotFoundException(dto.categoryId()));

        Budget budget = budgetMapper.toEntity(dto);

        budget.setOwner(user);
        budget.setCategory(category);

        budgetRepository.save(budget);

        return budgetMapper.toDto(budget);
    }

    public List<BudgetResponseDto> getAllBudgets() {

        User user = securityUtils.getCurrentUser();

        return budgetRepository.findByOwnerId(user.getId())
                .stream()
                .map(budgetMapper::toDto)
                .toList();
    }

    public BudgetResponseDto getBudgetById(Long id) {

        User user = securityUtils.getCurrentUser();

        Budget budget = budgetRepository.findByIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new BudgetNotFoundException(id));

        return budgetMapper.toDto(budget);
    }

    public BudgetResponseDto updateBudget(Long id, BudgetRequestDto dto) {

        User user = securityUtils.getCurrentUser();

        Budget budget = budgetRepository.findByIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new BudgetNotFoundException(id));

        Category category = categoryRepository.findByIdAndOwnerId(dto.categoryId(), user.getId())
                .orElseThrow(() -> new CategoryNotFoundException(dto.categoryId()));

        budget.setCategory(category);
        budget.setLimitAmount(dto.limitAmount());
        budget.setStartDate(dto.startDate());
        budget.setEndDate(dto.endDate());
        budget.setPeriod(dto.period());

        budgetRepository.save(budget);

        return budgetMapper.toDto(budget);
    }

    public void deleteBudget(Long id) {

        User user = securityUtils.getCurrentUser();

        Budget budget = budgetRepository.findByIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new BudgetNotFoundException(id));

        budgetRepository.delete(budget);
    }
}

