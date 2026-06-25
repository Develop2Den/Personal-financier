package com.d2d.personal_financier.ai.service;

import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import com.d2d.personal_financier.dto.analytics.DashboardDto;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class PromptBuilderService {

    private static final String ACCOUNT_TEMPLATE = """
        Name: %s
        Type: %s
        Balance: %s
        Currency: %s
        """;

    private static final String SYSTEM_RULES = """
        You are Personal Financier AI.

        Important rules:

        - Answer ONLY questions related to the user's personal finances.
        - Use ONLY the provided financial data.
        - Never invent balances.
        - Never modify balances.
        - Never invent currencies.
        - Never infer or estimate financial information.
        - Never convert currencies unless exchange rates are explicitly provided.
        - If accounts use different currencies, NEVER calculate one total balance across all accounts.
        - Group balances by currency instead.
        - If information is unavailable, explicitly say that it is unavailable.
        - Be concise, accurate and professional.
        """;

    public String buildPrompt(
        FinancialContextDto context,
        String question) {

        DashboardDto dashboard = context.dashboard();

        String accounts = context.accounts()
            .stream()
            .map(account -> ACCOUNT_TEMPLATE.formatted(
                account.name(),
                account.type(),
                account.balance(),
                account.currency()
            ))
            .collect(Collectors.joining("\n------------------\n"));

        return SYSTEM_RULES + """

            === FINANCIAL SUMMARY ===

            Monthly Income: %s
            Monthly Expenses: %s
            Net Cashflow: %s

            Top Expense Category: %s
            Active Goals: %d
            Monthly Transaction Count: %d

            === ACCOUNTS ===

            %s

            === USER QUESTION ===

            %s
            """
            .formatted(
                dashboard.monthlyIncome(),
                dashboard.monthlyExpenses(),
                dashboard.netCashflow(),
                dashboard.topExpenseCategory(),
                dashboard.activeGoals(),
                dashboard.monthlyTransactionCount(),
                accounts,
                question
            );
    }
}
