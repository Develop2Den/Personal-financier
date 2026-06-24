package com.d2d.personal_financier.ai.service;

import com.d2d.personal_financier.ai.provider.AiProvider;
import com.d2d.personal_financier.ai.provider.AiProviderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final AiProviderFactory aiProviderFactory;
    private final FinancialContextService financialContextService;

    @Override
    public String ask(String question) {

        String context = financialContextService.buildContext();

        String prompt = """
            You are a financial assistant.

            Financial data:
            %s

            User question:
            %s
            """.formatted(context, question);

        AiProvider provider = aiProviderFactory.getProvider();

        return provider.ask(prompt);
    }
}
