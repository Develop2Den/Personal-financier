package com.d2d.personal_financier.controller;

import com.d2d.personal_financier.dto.analytics.CategoryBreakdownDto;
import com.d2d.personal_financier.dto.analytics.DashboardDto;
import com.d2d.personal_financier.dto.analytics.MonthlyCashflowDto;
import com.d2d.personal_financier.dto.analytics.MonthlyExpenseDto;
import com.d2d.personal_financier.dto.error.ErrorResponse;
import com.d2d.personal_financier.service.AnalyticsService;
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
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/monthly-cashflow")
    @Operation(summary = "Get monthly cashflow")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Monthly cashflow retrieved successfully"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<MonthlyCashflowDto>> getMonthlyCashflow() {

        return ResponseEntity.ok(
                analyticsService.getMonthlyCashflow()
        );
    }

    @GetMapping("/top-categories")
    @Operation(summary = "Get top expense categories for a selected month")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Top categories retrieved successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid month format. Expected YYYY-MM",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<CategoryBreakdownDto>> getTopCategories(
        @RequestParam(required = false) String month) {

        return ResponseEntity.ok(
                analyticsService.getTopCategories(month)
        );
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard data for a selected month")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid month format. Expected YYYY-MM",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<DashboardDto> getDashboard(
        @RequestParam(required = false) String month) {

        return ResponseEntity.ok(
                analyticsService.getDashboard(month)
        );
    }
}
