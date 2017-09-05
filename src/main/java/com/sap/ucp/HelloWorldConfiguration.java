package com.sap.ucp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class HelloWorldConfiguration extends SpringBootServletInitializer {

    public static final int TWO_SECONDS = (int) TimeUnit.SECONDS.toMillis(2);

    public static void main(String[] args) {

        SpringApplication.run(HelloWorldConfiguration.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder()
                .requestFactory(buildClientHttpRequestFactory());
    }

    private ClientHttpRequestFactory buildClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory
                = new HttpComponentsClientHttpRequestFactory();

        factory.setConnectTimeout(TWO_SECONDS);
        factory.setReadTimeout(TWO_SECONDS);
        factory.setConnectionRequestTimeout(TWO_SECONDS);
        return factory;
    }
}
