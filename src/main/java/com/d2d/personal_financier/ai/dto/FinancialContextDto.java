package com.d2d.personal_financier.ai.dto;

import com.d2d.personal_financier.dto.analytics.DashboardDto;

import java.util.List;

public record FinancialContextDto(

    DashboardDto dashboard,

    List<AccountSummaryDto> accounts

//    List<GoalSummaryDto> goals,
//
//    List<TransactionSummaryDto> recentTransactions,
//
//    List<CategorySummaryDto> topCategories

) {
}
