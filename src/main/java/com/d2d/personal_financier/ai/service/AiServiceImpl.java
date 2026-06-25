package com.d2d.personal_financier.ai.service;

import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import com.d2d.personal_financier.ai.provider.AiProvider;
import com.d2d.personal_financier.ai.provider.AiProviderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final AiProviderFactory aiProviderFactory;
    private final FinancialContextService financialContextService;
    private final PromptBuilderService promptBuilderService;

    @Override
    public String ask(String question) {

        FinancialContextDto context = financialContextService.buildContext();

        String prompt = promptBuilderService.buildPrompt(context, question);

        AiProvider provider = aiProviderFactory.getProvider();

        return provider.ask(prompt);
    }
}
