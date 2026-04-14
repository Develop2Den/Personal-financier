package com.D2D.personal_financier.controller;

import com.D2D.personal_financier.dto.analytics.CategoryReportDto;
import com.D2D.personal_financier.dto.analytics.DashboardDto;
import com.D2D.personal_financier.dto.analytics.MonthlyExpenseDto;
import com.D2D.personal_financier.dto.error.ErrorResponse;
import com.D2D.personal_financier.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Financial analytics")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/monthly-expenses")
    @Operation(summary = "Get monthly expenses")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Monthly expenses retrieved successfully"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<MonthlyExpenseDto>> getMonthlyExpenses() {

        return ResponseEntity.ok(
                analyticsService.getMonthlyExpenses()
        );
    }

    @GetMapping("/top-categories")
    @Operation(summary = "Get top categories")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Top categories retrieved successfully"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<CategoryReportDto>> getTopCategories() {

        return ResponseEntity.ok(
                analyticsService.getTopCategories()
        );
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<DashboardDto> getDashboard() {

        return ResponseEntity.ok(
                analyticsService.getDashboard()
        );
    }
}
