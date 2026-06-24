package com.d2d.personal_financier.ai.provider;

import org.springframework.stereotype.Component;

@Component
public class MockAiProvider implements AiProvider {

    @Override
    public String ask(String prompt) {
        return "Mock AI response: " + prompt;
    }
}
