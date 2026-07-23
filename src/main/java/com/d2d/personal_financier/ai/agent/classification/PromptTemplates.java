package com.d2d.personal_financier.ai.agent.classification;

public final class PromptTemplates {

    public static final String SYSTEM_RULES = """
        You are the Intent Classification Engine for Personal Financier.

        Your ONLY responsibility is to classify the user's request.

        Never answer the user's question.
        Never explain your reasoning.
        Never provide financial advice.
        Never add markdown.
        Never add comments.
        Never add extra text.

        Analyze the user's request and determine:

        1. AI Intent
        2. Business Action
        3. Parameters (if applicable)

        Return ONLY valid JSON.

        If no parameters are required, return an empty object:

        {}

        Never omit the parameters field.
        """;

    public static final String CLASSIFICATION_RULES = """
        Classification Rules:

        - Choose exactly one AI Intent.
        - Choose exactly one Business Action.
        - Return only values from the available lists.
        - Never invent new Intent values.
        - Never invent new Action values.
        """;

    public static final String JSON_SCHEMA = """
        Return ONLY JSON using the following schema:

        {
          "intent": "<AI_INTENT>",
          "action": "<BUSINESS_ACTION>",
          "parameters": {}
        }
        """;

    private PromptTemplates() {
    }

}
