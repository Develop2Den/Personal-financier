package com.D2D.personal_financier.service;

import com.D2D.personal_financier.config.security.SecurityUtils;
import com.D2D.personal_financier.dto.budgetDTO.BudgetRequestDto;
import com.D2D.personal_financier.dto.budgetDTO.BudgetResponseDto;
import com.D2D.personal_financier.entity.Budget;
import com.D2D.personal_financier.entity.Category;
import com.D2D.personal_financier.entity.User;
import com.D2D.personal_financier.mapper.BudgetMapper;
import com.D2D.personal_financier.repository.BudgetRepository;
import com.D2D.personal_financier.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetMapper budgetMapper;
    private final SecurityUtils securityUtils;

    public BudgetResponseDto createBudget(BudgetRequestDto dto) {

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Budget budget = budgetMapper.toEntity(dto);

        User user = securityUtils.getCurrentUser();

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

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        return budgetMapper.toDto(budget);
    }

    public BudgetResponseDto updateBudget(Long id, BudgetRequestDto dto) {

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        budget.setCategory(category);
        budget.setLimitAmount(dto.limitAmount());
        budget.setStartDate(dto.startDate());
        budget.setEndDate(dto.endDate());
        budget.setPeriod(dto.period());

        budgetRepository.save(budget);

        return budgetMapper.toDto(budget);
    }

    public void deleteBudget(Long id) {

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        budgetRepository.delete(budget);
    }
}

