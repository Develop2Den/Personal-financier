package com.d2d.personal_financier.ai.agent.agents.analytics;

import com.d2d.personal_financier.ai.agent.agents.analytics.model.AgentExecution;
import com.d2d.personal_financier.ai.agent.agents.analytics.model.AiIntent;
import com.d2d.personal_financier.ai.agent.agents.analytics.model.ResolvedIntent;
import com.d2d.personal_financier.ai.agent.agents.analytics.prompt.AnalyticsPromptBuilderService;
import com.d2d.personal_financier.ai.agent.infrastructure.executor.AiAgent;
import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import com.d2d.personal_financier.ai.service.FinancialContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsAgent implements AiAgent {

    private final FinancialContextService financialContextService;
    private final AnalyticsPromptBuilderService analyticsPromptBuilderService;

    @Override
    public AiIntent supportedIntent() {
        return AiIntent.ANALYTICS;
    }

    @Override
    public AgentExecution execute(ResolvedIntent resolvedIntent) {

        log.info(
            "AnalyticsAgent executed. Action: {}",
            resolvedIntent.action()
        );

        FinancialContextDto context =
            financialContextService.buildContext();

        String prompt =
            analyticsPromptBuilderService.buildPrompt(
                context,
                resolvedIntent.userMessage()
            );

        return new AgentExecution(prompt);
    }
}
