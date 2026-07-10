package com.d2d.personal_financier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class CurrencyConfig {

    @Bean
    public RestClient currencyRestClient(RestClient.Builder builder) {
        return builder.build();
    }
}
