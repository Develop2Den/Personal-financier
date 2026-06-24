package com.d2d.personal_financier.ai.provider.gemini;


import java.util.List;

public record GeminiRequest(

    List<Content> contents

) {

    public record Content(

        List<Part> parts

    ) {
    }

    public record Part(

        String text

    ) {
    }
}
