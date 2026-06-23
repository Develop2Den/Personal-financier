package com.d2d.personal_financier.integration;

import com.d2d.personal_financier.config.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class IntegrationTest
    extends PostgresTestContainer {

    @Test
    void contextLoads() {
    }
}
