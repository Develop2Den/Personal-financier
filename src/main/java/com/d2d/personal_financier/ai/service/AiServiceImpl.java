package com.d2d.personal_financier.ai.service;

import com.d2d.personal_financier.ai.agent.AiAgentService;
import com.d2d.personal_financier.ai.agent.analytics.model.AiRequest;
import com.d2d.personal_financier.ai.agent.analytics.model.AiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final AiAgentService aiAgentService;

    @Override
    public String ask(String question) {

        AiResponse response =
            aiAgentService.execute(
                new AiRequest(question)
            );

        return response.content();
    }
}
