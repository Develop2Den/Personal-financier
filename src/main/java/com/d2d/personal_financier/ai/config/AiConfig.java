package com.d2d.personal_financier.ai.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class AiConfig {

    @Bean
    public RestClient restClient(
        @Qualifier("outboundHttpRequestFactory") ClientHttpRequestFactory requestFactory
    ) {
        return RestClient.builder().requestFactory(requestFactory).build();
    }
}
