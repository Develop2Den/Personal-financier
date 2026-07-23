package com.d2d.personal_financier.ai.agent.infrastructure.executor;

import com.d2d.personal_financier.ai.agent.analytics.model.AgentExecution;
import com.d2d.personal_financier.ai.agent.analytics.model.AiIntent;
import com.d2d.personal_financier.ai.agent.analytics.model.ResolvedIntent;

public interface AiAgent {

    AiIntent supportedIntent();

    AgentExecution execute(ResolvedIntent resolvedIntent);

}
