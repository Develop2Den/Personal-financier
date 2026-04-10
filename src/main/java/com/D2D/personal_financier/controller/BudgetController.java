package com.D2D.personal_financier.controller;

import com.D2D.personal_financier.dto.budgetDTO.BudgetRequestDto;
import com.D2D.personal_financier.dto.budgetDTO.BudgetResponseDto;
import com.D2D.personal_financier.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "Budgets", description = "Operations with financial budgets")
@SecurityRequirement(name = "bearerAuth")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @Operation(summary = "Create a new budget")
    public ResponseEntity<BudgetResponseDto> createBudget(
            @RequestBody BudgetRequestDto dto) {

        BudgetResponseDto response = budgetService.createBudget(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @Operation(summary = "Get all budgets")
    public ResponseEntity<List<BudgetResponseDto>> getAllBudgets() {

        return ResponseEntity.ok(budgetService.getAllBudgets());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get budget by ID")
    public ResponseEntity<BudgetResponseDto> getBudgetById(
            @PathVariable Long id) {

        return ResponseEntity.ok(budgetService.getBudgetById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update budget")
    public ResponseEntity<BudgetResponseDto> updateBudget(
            @PathVariable Long id,
            @RequestBody BudgetRequestDto dto) {

        return ResponseEntity.ok(budgetService.updateBudget(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete budget")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long id) {

        budgetService.deleteBudget(id);

        return ResponseEntity.noContent().build();
    }
}
