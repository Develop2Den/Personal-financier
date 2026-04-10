package com.D2D.personal_financier.controller;

import com.D2D.personal_financier.dto.analytics.CategoryReportDto;
import com.D2D.personal_financier.dto.analytics.DashboardDto;
import com.D2D.personal_financier.dto.analytics.MonthlyExpenseDto;
import com.D2D.personal_financier.service.AnalyticsService;
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
    public ResponseEntity<List<MonthlyExpenseDto>> getMonthlyExpenses() {

        return ResponseEntity.ok(
                analyticsService.getMonthlyExpenses()
        );
    }

    @GetMapping("/top-categories")
    public ResponseEntity<List<CategoryReportDto>> getTopCategories() {

        return ResponseEntity.ok(
                analyticsService.getTopCategories()
        );
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getDashboard() {

        return ResponseEntity.ok(
                analyticsService.getDashboard()
        );
    }
}
