package com.d2d.personal_financier.ai.agent.infrastructure.resolver;

import org.springframework.stereotype.Component;

@Component
public class ActionResolver {

    public <T extends Enum<T>> T resolve(
        Class<T> actionClass,
        String action) {

        try {

            return Enum.valueOf(
                actionClass,
                action
            );

        } catch (IllegalArgumentException ex) {

            throw new UnsupportedOperationException(
                "Unsupported action '%s' for %s"
                    .formatted(
                        action,
                        actionClass.getSimpleName()
                    ),
                ex
            );
        }
    }

}
