package com.d2d.personal_financier.ai.parser;

import com.d2d.personal_financier.ai.agent.analytics.model.AiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiJsonParser {

    private final ObjectMapper objectMapper;

    public <T> T parse(
        AiResponse response,
        Class<T> responseType) {

        try {

            return objectMapper.readValue(
                response.content(),
                responseType
            );

        } catch (JsonProcessingException ex) {

            throw new IllegalStateException(
                "Failed to parse AI response.",
                ex
            );
        }
    }

    public <T> T convert(
        JsonNode node,
        Class<T> clazz) {

        return objectMapper.convertValue(
            node,
            clazz
        );
    }

}
