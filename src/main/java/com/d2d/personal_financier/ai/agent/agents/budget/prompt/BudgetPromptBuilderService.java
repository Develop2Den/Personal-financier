package com.d2d.personal_financier.ai.agent.agents.budget.prompt;

import com.d2d.personal_financier.ai.agent.infrastructure.prompt.AgentPromptBuilder;
import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetPromptBuilderService {

    private static final String SYSTEM_INSTRUCTIONS = """
        You are a Budget AI Agent.

        Your responsibility is to answer ONLY budget-related questions.

        Use ONLY the budget information provided.

        Never invent budgets.
        Never invent budget limits.
        Never invent spending information.
        Never answer questions unrelated to user budgets.
        """;

    private final AgentPromptBuilder agentPromptBuilder;

    public String buildPrompt(
        FinancialContextDto context,
        String userQuestion) {

        return agentPromptBuilder.buildPrompt(
            userQuestion,
            SYSTEM_INSTRUCTIONS,
            context
        );
    }

}

