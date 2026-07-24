package com.d2d.personal_financier.ai.agent.agents.transaction.prompt;

import com.d2d.personal_financier.ai.agent.infrastructure.prompt.AgentPromptBuilder;
import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionPromptBuilderService {

    private static final String SYSTEM_INSTRUCTIONS = """
        You are a Transaction AI Agent.

        Your responsibility is to answer ONLY transaction-related questions.

        Use ONLY the transaction information provided.

        Never invent transactions.
        Never invent amounts.
        Never invent dates.
        Never answer questions unrelated to user transactions.
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
