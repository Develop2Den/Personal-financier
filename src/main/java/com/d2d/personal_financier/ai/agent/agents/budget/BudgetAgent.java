package com.d2d.personal_financier.ai.agent.agents.budget;

import com.d2d.personal_financier.ai.agent.agents.analytics.model.AgentExecution;
import com.d2d.personal_financier.ai.agent.agents.analytics.model.AiIntent;
import com.d2d.personal_financier.ai.agent.agents.analytics.model.ResolvedIntent;
import com.d2d.personal_financier.ai.agent.agents.budget.prompt.BudgetPromptBuilderService;
import com.d2d.personal_financier.ai.agent.infrastructure.executor.AiAgent;
import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import com.d2d.personal_financier.ai.service.FinancialContextService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BudgetAgent implements AiAgent {

    private static final Logger log =
        LoggerFactory.getLogger(BudgetAgent.class);

    private final FinancialContextService financialContextService;
    private final BudgetPromptBuilderService budgetPromptBuilderService;

    @Override
    public AiIntent supportedIntent() {
        return AiIntent.BUDGET;
    }

    @Override
    public AgentExecution execute(ResolvedIntent resolvedIntent) {

        log.info(
            "BudgetAgent executed. Action: {}",
            resolvedIntent.action()
        );

        FinancialContextDto context =
            financialContextService.buildContext();

        String prompt =
            budgetPromptBuilderService.buildPrompt(
                context,
                resolvedIntent.userMessage()
            );

        return new AgentExecution(prompt);
    }

}
