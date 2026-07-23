package com.d2d.personal_financier.ai.agent.infrastructure.resolver;

import com.d2d.personal_financier.ai.agent.analytics.model.AiRequest;
import com.d2d.personal_financier.ai.agent.analytics.model.ResolvedIntent;

public interface IntentResolver {

    ResolvedIntent resolve(AiRequest request);

}
