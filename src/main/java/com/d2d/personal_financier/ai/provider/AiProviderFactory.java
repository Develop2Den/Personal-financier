package com.d2d.personal_financier.ai.provider;

import com.d2d.personal_financier.ai.config.AiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiProviderFactory {

    private final AiProperties aiProperties;
    private final MockAiProvider mockAiProvider;
    private final GeminiProvider geminiProvider;
    
    public AiProvider getProvider() {

        return switch (aiProperties.getProvider()) {

            case MOCK -> mockAiProvider;

            case GEMINI -> geminiProvider;

            case DEEPSEEK -> throw new UnsupportedOperationException("DeepSeek provider is not implemented yet");
        };
    }
}
