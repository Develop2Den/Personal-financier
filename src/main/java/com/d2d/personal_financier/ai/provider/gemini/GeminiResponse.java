package com.d2d.personal_financier.ai.provider.gemini;

import java.util.List;

public record GeminiResponse(

    List<Candidate> candidates

) {

    public record Candidate(

        Content content

    ) {
    }

    public record Content(

        List<Part> parts

    ) {
    }

    public record Part(

        String text

    ) {
    }
}
