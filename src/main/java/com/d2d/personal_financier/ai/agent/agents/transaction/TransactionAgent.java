package com.d2d.personal_financier.ai.agent.agents.transaction;

import com.d2d.personal_financier.ai.agent.agents.analytics.model.AgentExecution;
import com.d2d.personal_financier.ai.agent.agents.analytics.model.AiIntent;
import com.d2d.personal_financier.ai.agent.agents.analytics.model.ResolvedIntent;
import com.d2d.personal_financier.ai.agent.agents.transaction.prompt.TransactionPromptBuilderService;
import com.d2d.personal_financier.ai.agent.infrastructure.executor.AiAgent;
import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import com.d2d.personal_financier.ai.service.FinancialContextService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionAgent implements AiAgent {

    private static final Logger log =
        LoggerFactory.getLogger(TransactionAgent.class);

    private final FinancialContextService financialContextService;
    private final TransactionPromptBuilderService transactionPromptBuilderService;

    @Override
    public AiIntent supportedIntent() {
        return AiIntent.TRANSACTION;
    }

    @Override
    public AgentExecution execute(ResolvedIntent resolvedIntent) {

        log.info(
            "TransactionAgent executed. Action: {}",
            resolvedIntent.action()
        );

        FinancialContextDto context =
            financialContextService.buildContext();

        String prompt =
            transactionPromptBuilderService.buildPrompt(
                context,
                resolvedIntent.userMessage()
            );

        return new AgentExecution(prompt);
    }

}
