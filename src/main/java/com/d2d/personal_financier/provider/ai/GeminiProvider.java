package com.d2d.personal_financier.provider.ai;

import com.d2d.personal_financier.ai.config.AiProperties;
import com.d2d.personal_financier.provider.ai.gemini.GeminiRequest;
import com.d2d.personal_financier.provider.ai.gemini.GeminiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiProvider implements AiProvider {

    private static final String GEMINI_MODEL =
        "gemini-2.5-flash";

    private final AiProperties aiProperties;
    private final RestClient restClient;

    @Override
    public String ask(String prompt) {

        try {

            String apiKey = aiProperties.getGemini().getApiKey();

            GeminiRequest request = new GeminiRequest(
                List.of(
                    new GeminiRequest.Content(
                        List.of(
                            new GeminiRequest.Part(prompt)
                        )
                    )
                )
            );

            GeminiResponse response = restClient.post()
                .uri(
                    "https://generativelanguage.googleapis.com/v1beta/models/"
                        + GEMINI_MODEL
                        + ":generateContent?key="
                        + apiKey
                )
                .body(request)
                .retrieve()
                .body(GeminiResponse.class);

            if (response == null
                || response.candidates() == null
                || response.candidates().isEmpty()) {

                return "Gemini returned empty response";
            }

            return response.candidates()
                .getFirst()
                .content()
                .parts()
                .getFirst()
                .text();

        } catch (HttpClientErrorException.TooManyRequests ex) {
            return "Gemini quota exceeded. Please check API limits or billing settings.";
        } catch (HttpServerErrorException.ServiceUnavailable ex) {
            return "Gemini service is temporarily overloaded. Try again later.";
        } catch (Exception ex) {
            log.error("Error while communicating with Gemini", ex);
            return "Error while communicating with Gemini: " + ex.getMessage();
        }


    }
}
