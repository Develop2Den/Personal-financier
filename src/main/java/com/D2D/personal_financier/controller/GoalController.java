package com.D2D.personal_financier.controller;

import com.D2D.personal_financier.dto.goalDTO.GoalRequestDto;
import com.D2D.personal_financier.dto.goalDTO.GoalResponseDto;
import com.D2D.personal_financier.dto.error.ErrorResponse;
import com.D2D.personal_financier.service.GoalService;
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
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Tag(name = "Goals", description = "Operations with financial goals")
@SecurityRequirement(name = "bearerAuth")
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    @Operation(summary = "Create a new goal")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Goal created successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<GoalResponseDto> createGoal(
            @Valid @RequestBody GoalRequestDto dto) {

        GoalResponseDto response = goalService.createGoal(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @Operation(summary = "Get all goals")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Goals retrieved successfully"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<GoalResponseDto>> getAllGoals() {

        return ResponseEntity.ok(goalService.getAllGoals());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goal by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Goal found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Goal not found with id: {id}",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<GoalResponseDto> getGoalById(
            @PathVariable Long id) {

        return ResponseEntity.ok(goalService.getGoalById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update goal")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Goal updated successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Goal not found with id: {id}",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<GoalResponseDto> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody GoalRequestDto dto) {

        return ResponseEntity.ok(goalService.updateGoal(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete goal")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Goal deleted successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Goal not found with id: {id}",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long id) {

        goalService.deleteGoal(id);

        return ResponseEntity.noContent().build();
    }
}
