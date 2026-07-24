package com.d2d.personal_financier.ai.agent.agents.account.prompt;

import com.d2d.personal_financier.ai.agent.infrastructure.prompt.AgentPromptBuilder;
import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountPromptBuilderService {

    private static final String SYSTEM_INSTRUCTIONS = """
        You are an Account AI Agent.

        Your responsibility is to answer ONLY account-related questions.

        Use ONLY the account information provided.

        Never invent balances.
        Never invent currencies.
        Never answer questions unrelated to user accounts.
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
