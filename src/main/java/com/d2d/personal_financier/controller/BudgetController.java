package com.d2d.personal_financier.controller;

import com.d2d.personal_financier.dto.budgetDTO.BudgetRequestDto;
import com.d2d.personal_financier.dto.budgetDTO.BudgetResponseDto;
import com.d2d.personal_financier.dto.error.ErrorResponse;
import com.d2d.personal_financier.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Budget created successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found with id: {categoryId}",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<BudgetResponseDto> createBudget(
            @Valid @RequestBody BudgetRequestDto dto) {

        BudgetResponseDto response = budgetService.createBudget(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @Operation(summary = "Get all budgets")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budgets retrieved successfully"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<BudgetResponseDto>> getAllBudgets() {

        return ResponseEntity.ok(budgetService.getAllBudgets());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get budget by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Budget not found with id: {id}",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<BudgetResponseDto> getBudgetById(
            @PathVariable Long id) {

        return ResponseEntity.ok(budgetService.getBudgetById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update budget")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget updated successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Budget not found with id: {id} or category not found with id: {categoryId}",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<BudgetResponseDto> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequestDto dto) {

        return ResponseEntity.ok(budgetService.updateBudget(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete budget")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Budget deleted successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Budget not found with id: {id}",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long id) {

        budgetService.deleteBudget(id);

        return ResponseEntity.noContent().build();
    }
}
