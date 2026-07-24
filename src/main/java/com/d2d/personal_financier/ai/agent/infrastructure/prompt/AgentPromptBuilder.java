package com.d2d.personal_financier.ai.agent.infrastructure.prompt;

import com.d2d.personal_financier.ai.dto.FinancialContextDto;
import org.springframework.stereotype.Service;

@Service
public class AgentPromptBuilder {

    public String buildPrompt(
        String userQuestion,
        String agentInstructions,
        FinancialContextDto context) {

        return """
            User request:

            %s

            %s

            Financial context:

            %s
            """
            .formatted(
                userQuestion,
                agentInstructions,
                context
            );
    }

}
