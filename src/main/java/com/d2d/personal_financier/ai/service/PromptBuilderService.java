package com.d2d.personal_financier.ai.service;

import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import com.d2d.personal_financier.dto.analytics.DashboardDto;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class PromptBuilderService {

    private static final String SECTION_SEPARATOR =
        "\n------------------\n";

    private static final String DASHBOARD_TEMPLATE = """
        Monthly Income: %s
        Monthly Expenses: %s
        Net Cashflow: %s

        Top Expense Category: %s
        Active Goals: %d
        Monthly Transaction Count: %d
        """;

    private static final String METRICS_TEMPLATE = """
        Analyzed Goal: %s
        Remaining Amount: %s
        Goal Completion: %s%%
        Largest Expense: %s
        Largest Income: %s
        Average Expense: %s
        Richest Account: %s
        Active Accounts: %s
        """;

    private static final String ACCOUNT_TEMPLATE = """
        Name: %s
        Type: %s
        Balance: %s
        Currency: %s
        """;

    private static final String GOAL_TEMPLATE = """
        Name: %s
        Target Amount: %s
        Current Amount: %s
        Deadline: %s
        Status: %s
        """;

    private static final String TRANSACTION_TEMPLATE = """
        Date: %s
        Type: %s
        Amount: %s
        Currency: %s
        Account: %s
        Category: %s
        Description: %s
        Transfer Direction: %s
        """;

    private static final String CATEGORY_TEMPLATE = """
        Name: %s
        Type: %s
        Active: %s
        """;

    private static final String SYSTEM_RULES = """
        You are Personal Financier AI.

        Important rules:

        GENERAL

        - Answer ONLY questions related to the user's personal finances.
        - Answer in the same language as the user's question.
        - Be concise, accurate and professional.

        FINANCIAL DATA

        - Use ONLY the provided financial data.
        - Never invent balances.
        - Never modify balances.
        - Never invent currencies.
        - Never invent financial goals.
        - Never infer or estimate financial information.
        - Never convert currencies unless exchange rates are explicitly provided.
        - If accounts use different currencies, NEVER calculate one total balance across all accounts.
        - Keep all account names, goal names, category names and currencies exactly as provided in the financial data.
        - Do NOT translate account names, goal names, category names or currencies unless the user explicitly requests a translation.

        FINANCIAL METRICS

        - The FINANCIAL METRICS section contains values calculated by the backend.
        - Always trust FINANCIAL METRICS over your own calculations.
        - Do not recalculate values that already exist in FINANCIAL METRICS.
        - The analyzed goal is selected automatically by the backend as the nearest ACTIVE goal by deadline.
        - If the user asks why this goal is being analyzed, explain that it was automatically selected because it has the nearest deadline among active goals.

        RESPONSES

        - Use exact numbers whenever they are available.
        - Prefer concrete financial values over vague phrases like "almost", "soon", or "a lot".
        - If information is unavailable, explicitly say that it is unavailable.
        - Do not claim you selected the analyzed goal yourself. The backend selects it automatically.
        """;

    public String buildPrompt(
        FinancialContextDto context,
        String question) {

        DashboardDto dashboard = context.dashboard();

        String dashboardSummary = DASHBOARD_TEMPLATE.formatted(
            dashboard.monthlyIncome(),
            dashboard.monthlyExpenses(),
            dashboard.netCashflow(),
            dashboard.topExpenseCategory(),
            dashboard.activeGoals(),
            dashboard.monthlyTransactionCount()
        );

        String metrics = METRICS_TEMPLATE.formatted(
            context.metrics().analyzedGoalName(),
            context.metrics().remainingGoalAmount(),
            context.metrics().goalCompletionPercentage(),
            context.metrics().largestExpense(),
            context.metrics().largestIncome(),
            context.metrics().averageExpense(),
            context.metrics().richestAccount(),
            context.metrics().activeAccounts()
        );

        String accounts = context.accounts()
            .stream()
            .map(account -> ACCOUNT_TEMPLATE.formatted(
                account.name(),
                account.type(),
                account.balance(),
                account.currency()
            ))
            .collect(Collectors.joining(SECTION_SEPARATOR));

        String goals = context.goals()
            .stream()
            .map(goal -> GOAL_TEMPLATE.formatted(
                goal.name(),
                goal.targetAmount(),
                goal.currentAmount(),
                goal.deadline(),
                goal.status()
            ))
            .collect(Collectors.joining(SECTION_SEPARATOR));

        String transactions = context.transactions()
            .stream()
            .map(transaction -> TRANSACTION_TEMPLATE.formatted(
                transaction.date(),
                transaction.type(),
                transaction.amount(),
                transaction.currency(),
                transaction.account(),
                transaction.category(),
                transaction.description(),
                transaction.transferDirection()
            ))
            .collect(Collectors.joining(SECTION_SEPARATOR));

        String categories = context.categories()
            .stream()
            .map(category -> CATEGORY_TEMPLATE.formatted(
                category.name(),
                category.type(),
                category.active()
            ))
            .collect(Collectors.joining(SECTION_SEPARATOR));

        return SYSTEM_RULES + """

            === FINANCIAL SUMMARY ===

            %s

            === FINANCIAL METRICS ===

            %s

            === ACCOUNTS ===

            %s

            === GOALS ===

            %s

            === TRANSACTIONS ===

            %s

            === CATEGORIES ===

            %s

            === USER QUESTION ===

            %s
            """
            .formatted(
                dashboardSummary,
                metrics,
                accounts,
                goals,
                transactions,
                categories,
                question
            );
    }
}
