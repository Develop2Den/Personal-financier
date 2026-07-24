package com.d2d.personal_financier.ai.agent.agents.analytics.prompt;

import com.d2d.personal_financier.ai.agent.infrastructure.prompt.AgentPromptBuilder;
import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsPromptBuilderService {

    private static final String ANALYTICS_INSTRUCTIONS = """
        You are an Analytics AI Agent.

        Your responsibility is to answer ONLY analytics-related questions.

        Use ONLY the financial information provided.

        Never invent values.
        Never estimate numbers.
        Never answer questions outside the financial analytics domain.
        """;

    private final AgentPromptBuilder agentPromptBuilder;

    public String buildPrompt(
        FinancialContextDto context,
        String userQuestion) {

        return agentPromptBuilder.buildPrompt(
            userQuestion,
            ANALYTICS_INSTRUCTIONS,
            context
        );
    }

}
