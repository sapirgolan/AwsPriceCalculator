package com.sap.ucp.web;

import com.sap.ucp.model.OrderUcp;
import com.sap.ucp.model.PriceEstimation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.junit4.SpringRunner;

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
        HttpEntity<OrderUcp> order = new HttpEntity<>(new OrderUcp("t2.large", "Oregon"));
        PriceEstimation priceEstimation = restTemplate.postForObject(HTTP_LOCALHOST + port + CONTROLLER_NAME, order, PriceEstimation.class);
        assertThat(priceEstimation).hasNoNullFieldsOrPropertiesExcept("price", "currency");
        assertThat(priceEstimation.getCurrency()).matches("EUR");
        assertThat(priceEstimation.getPrice()).isBetween(2.325 * 30, 6.975 * 30);
    }
}