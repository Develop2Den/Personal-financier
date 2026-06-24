package com.d2d.personal_financier.ai.config;

import com.d2d.personal_financier.ai.provider.AiProviderType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    private AiProviderType provider = AiProviderType.MOCK;

    private Gemini gemini = new Gemini();

    @Getter
    @Setter
    public static class Gemini {

        private String apiKey;

    }

}
