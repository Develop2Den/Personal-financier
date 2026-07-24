package com.d2d.personal_financier.ai.agent.agents.category.prompt;


import com.d2d.personal_financier.ai.agent.infrastructure.prompt.AgentPromptBuilder;
import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryPromptBuilderService {

    private static final String SYSTEM_INSTRUCTIONS = """
    You are a Category AI Agent.

    Your responsibility is to answer ONLY category-related questions.

    Use ONLY the category information provided.

    Never invent categories.
    Never invent category types.
    Never answer questions unrelated to user categories.
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
