package com.D2D.personal_financier.service;

import com.D2D.personal_financier.dto.budgetDTO.BudgetRequestDto;
import com.D2D.personal_financier.dto.budgetDTO.BudgetResponseDto;
import com.D2D.personal_financier.entity.Budget;
import com.D2D.personal_financier.entity.Category;
import com.D2D.personal_financier.mapper.BudgetMapper;
import com.D2D.personal_financier.repository.BudgetRepository;
import com.D2D.personal_financier.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private  final CategoryRepository categoryRepository;
    private final BudgetMapper budgetMapper;

    public BudgetResponseDto createBudget(BudgetRequestDto dto) {
        Budget budget = budgetMapper.toEntity(dto);
        budgetRepository.save(budget);
        return budgetMapper.toDto(budget);
    }

    public List<BudgetResponseDto> getAllBudgets() {
        return budgetRepository.findAll().stream()
                .map(budgetMapper::toDto)
                .collect(Collectors.toList());
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
        budgetRepository.deleteById(id);
    }
}

