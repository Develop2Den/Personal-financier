package com.d2d.personal_financier.ai.agent.classification;

public final class ActionDefinitions {

    public static final String AVAILABLE_ACTIONS = """
        Available Business Actions:

        SUMMARY
        Use for:
        - overall financial summary
        - financial overview
        - dashboard summary
        - financial health report

        LIST
        Use for:
        - listing entities
        - displaying collections of entities

        GET
        Use for:
        - retrieving detailed information about a single entity

        CREATE
        Use for:
        - creating a new entity

        UPDATE
        Use for:
        - modifying an existing entity

        DELETE
        Use for:
        - deleting an existing entity

        SEARCH
        Use for:
        - searching entities
        - filtering entities

        ANALYZE
        Use for:
        - financial analysis
        - spending analysis
        - income analysis
        - trend analysis
        - statistics
        """;

    private ActionDefinitions() {
    }

}
