package com.D2D.personal_financier.controller;

import com.D2D.personal_financier.dto.goalDTO.GoalRequestDto;
import com.D2D.personal_financier.dto.goalDTO.GoalResponseDto;
import com.D2D.personal_financier.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Tag(name = "Goals", description = "Operations with financial goals")
@SecurityRequirement(name = "bearerAuth")
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    @Operation(summary = "Create a new goal")
    public ResponseEntity<GoalResponseDto> createGoal(
            @RequestBody GoalRequestDto dto) {

        GoalResponseDto response = goalService.createGoal(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @Operation(summary = "Get all goals")
    public ResponseEntity<List<GoalResponseDto>> getAllGoals() {

        return ResponseEntity.ok(goalService.getAllGoals());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goal by ID")
    public ResponseEntity<GoalResponseDto> getGoalById(
            @PathVariable Long id) {

        return ResponseEntity.ok(goalService.getGoalById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update goal")
    public ResponseEntity<GoalResponseDto> updateGoal(
            @PathVariable Long id,
            @RequestBody GoalRequestDto dto) {

        return ResponseEntity.ok(goalService.updateGoal(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete goal")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long id) {

        goalService.deleteGoal(id);

        return ResponseEntity.noContent().build();
    }
}
