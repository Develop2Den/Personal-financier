package com.d2d.personal_financier.ai.service;

import com.d2d.personal_financier.ai.agent.agents.analytics.model.AiResponse;
import com.d2d.personal_financier.provider.ai.AiProvider;
import com.d2d.personal_financier.provider.ai.AiProviderFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiInferenceService {

    private static final Logger log =
        LoggerFactory.getLogger(AiInferenceService.class);

    private final AiProviderFactory aiProviderFactory;

    public AiResponse ask(String prompt) {

        AiProvider provider = aiProviderFactory.getProvider();

        String response = provider.ask(prompt);

        log.info("""

                ========== RAW AI RESPONSE ==========

                {}

                =====================================
                """,
            response
        );

        return new AiResponse(response);
    }

}
