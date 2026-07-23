package com.d2d.personal_financier.ai.agent.classification;

public final class ClassificationExamples {

    public static final String EXAMPLES = """
        Examples:

        User:
        Show my accounts.

        JSON:
        {
          "intent": "ACCOUNT",
          "action": "LIST",
          "parameters": {}
        }

        User:
        Show my account balance.

        JSON:
        {
          "intent": "ACCOUNT",
          "action": "GET",
          "parameters": {}
        }

        User:
        Show my latest transactions.

        JSON:
        {
          "intent": "TRANSACTION",
          "action": "LIST",
          "parameters": {}
        }

        User:
        Summarize my financial situation.

        JSON:
        {
          "intent": "ANALYTICS",
          "action": "SUMMARY",
          "parameters": {}
        }

        User:
        Analyze my spending.

        JSON:
        {
          "intent": "ANALYTICS",
          "action": "ANALYZE",
          "parameters": {}
        }

        User:
        Show my goals.

        JSON:
        {
          "intent": "GOAL",
          "action": "LIST",
          "parameters": {}
        }

        User:
        Show my budgets.

        JSON:
        {
          "intent": "BUDGET",
          "action": "LIST",
          "parameters": {}
        }

        User:
        What is the USD exchange rate?

        JSON:
        {
          "intent": "CURRENCY",
          "action": "GET",
          "parameters": {}
        }

        User:
        Convert 100 USD to UAH.

        JSON:
        {
          "intent": "CURRENCY",
          "action": "ANALYZE",
          "parameters": {}
        }

        User:
        Hello!

        JSON:
        {
          "intent": "SYSTEM",
          "action": "GET",
          "parameters": {}
        }
        """;

    private ClassificationExamples() {
    }

}
