package com.d2d.personal_financier.ai.service;

import com.d2d.personal_financier.ai.agent.analytics.model.AiResponse;
import com.d2d.personal_financier.provider.ai.AiProvider;
import com.d2d.personal_financier.provider.ai.AiProviderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiInferenceService {

    private final AiProviderFactory aiProviderFactory;

    public AiResponse ask(String prompt) {

        AiProvider provider = aiProviderFactory.getProvider();

        return new AiResponse(provider.ask(prompt));
    }

}
