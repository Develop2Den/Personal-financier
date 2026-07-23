package com.d2d.personal_financier.ai.agent.classification;

public final class IntentDefinitions {

    public static final String AVAILABLE_INTENTS = """
        Available AI Intents:

        ANALYTICS
        Use for:
        - financial summary
        - financial overview
        - dashboard
        - financial health
        - spending analysis
        - income analysis
        - savings analysis
        - goal progress
        - cash flow analysis

        ACCOUNT
        Use for:
        - list accounts
        - account balance
        - account information
        - create account
        - update account
        - delete account

        TRANSACTION
        Use for:
        - list transactions
        - create transaction
        - update transaction
        - delete transaction
        - transfer between accounts

        CATEGORY
        Use for:
        - list categories
        - create category
        - update category
        - delete category

        GOAL
        Use for:
        - list goals
        - create goal
        - update goal
        - delete goal
        - goal information

        BUDGET
        Use for:
        - list budgets
        - create budget
        - update budget
        - delete budget
        - budget information

        CURRENCY
        Use for:
        - exchange rates
        - currency conversion

        DASHBOARD
        Use ONLY when the user explicitly asks to display the dashboard screen.

        SYSTEM
        Use only for:
        - greetings
        - help
        - unsupported requests
        - questions unrelated to personal finance
        """;

    private IntentDefinitions() {
    }

}
