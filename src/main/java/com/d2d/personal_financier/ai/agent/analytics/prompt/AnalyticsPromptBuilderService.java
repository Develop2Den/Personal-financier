package com.d2d.personal_financier.ai.agent.analytics.prompt;

import com.d2d.personal_financier.ai.agent.analytics.AnalyticsAction;
import com.d2d.personal_financier.ai.agent.analytics.model.ResolvedIntent;
import com.d2d.personal_financier.ai.agent.infrastructure.resolver.ActionResolver;
import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import com.d2d.personal_financier.ai.service.FinancialContextPromptBuilderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsPromptBuilderService {

    private final ActionResolver actionResolver;
    private final FinancialContextPromptBuilderService
        financialContextPromptBuilderService;

    public String buildPrompt(
        FinancialContextDto context,
        ResolvedIntent resolvedIntent) {

        AnalyticsAction action =
            actionResolver.resolve(
                AnalyticsAction.class,
                resolvedIntent.action()
            );

        return switch (action) {

            case SUMMARY -> buildSummaryPrompt(
                context,
                resolvedIntent
            );
        };
    }

    private String buildSummaryPrompt(
        FinancialContextDto context,
        ResolvedIntent resolvedIntent) {

        return financialContextPromptBuilderService.buildPrompt(
            context,
            resolvedIntent.userMessage()
        );
    }
}
