package com.d2d.personal_financier.ai.agent.agents.goal.prompt;

import com.d2d.personal_financier.ai.agent.infrastructure.prompt.AgentPromptBuilder;
import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoalPromptBuilderService {

    private static final String SYSTEM_INSTRUCTIONS = """
        You are a Goal AI Agent.

        Your responsibility is to answer ONLY goal-related questions.

        Use ONLY the goal information provided.

        Never invent goals.
        Never invent target amounts.
        Never invent progress.
        Never answer questions unrelated to user goals.
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
