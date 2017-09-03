package com.sap.ucp.web;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.sap.ucp.model.OrderUcp;
import com.sap.ucp.model.PriceEstimation;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PriceControllerRestTest {

    public static final String HTTP_LOCALHOST = "http://localhost:";
    public static final String CONTROLLER_NAME = "/v1.0/aws";
    @Autowired
    PriceController priceController;
    @LocalServerPort
    private int port;
    /*All REST requests are being invoked using this member*/
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void sanity() throws Exception {
        HttpEntity<OrderUcp> order = new HttpEntity<>(new OrderUcp("x.Large"));
        PriceEstimation priceEstimation = restTemplate.postForObject(HTTP_LOCALHOST + port + CONTROLLER_NAME, order, PriceEstimation.class);
        assertThat(priceEstimation).hasNoNullFieldsOrPropertiesExcept("price", "currency");
        assertThat(priceEstimation.getCurrency()).matches("EUR");
        assertThat(priceEstimation.getPrice()).isCloseTo(414.18, Offset.offset(0.0001));
    }

    //    @TestConfiguration
    static class Config {
        @Bean
        public RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder()
                    .additionalMessageConverters(buildJackson2HttpConvetor());
        }

        @Bean
        public RestTemplate restTemplate(RestTemplateBuilder builder) {
            return builder.build();
        }

        private MappingJackson2HttpMessageConverter buildJackson2HttpConvetor() {
            MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
            jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            return jsonHttpMessageConverter;
        }

    }

}