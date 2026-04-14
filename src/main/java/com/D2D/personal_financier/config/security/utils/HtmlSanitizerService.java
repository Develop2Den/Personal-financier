package com.D2D.personal_financier.config.security.utils;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Component;

@Component
public class HtmlSanitizerService {

    private final PolicyFactory policy = Sanitizers.FORMATTING
        .and(Sanitizers.LINKS)
        .and(Sanitizers.BLOCKS);

    public String sanitize(String input) {

        if (input == null) {
            return null;
        }

        return policy.sanitize(input);
    }
}
