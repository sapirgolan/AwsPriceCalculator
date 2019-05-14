package com.sap.ucp;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

@EnableAsync

@SpringBootApplication
public class HelloWorldConfiguration extends SpringBootServletInitializer {

    public static final int FIVE_SECONDS = (int) TimeUnit.SECONDS.toMillis(5);

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
                .requestFactory(this::buildClientHttpRequestFactory);
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("CurrencyLookup-");
        executor.initialize();
        return executor;
    }

    private ClientHttpRequestFactory buildClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory
                = new HttpComponentsClientHttpRequestFactory();

        factory.setConnectTimeout(FIVE_SECONDS);
        factory.setReadTimeout(FIVE_SECONDS);
        factory.setConnectionRequestTimeout(FIVE_SECONDS);
        return factory;
    }
}
