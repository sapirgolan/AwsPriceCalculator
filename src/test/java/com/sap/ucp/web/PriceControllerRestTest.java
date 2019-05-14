package com.sap.ucp.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.sap.ucp.model.OrderUcp;
import com.sap.ucp.model.PriceEstimation;
import com.sap.ucp.types.OSType;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PriceControllerRestTest {

    public static final String HTTP_LOCALHOST = "http://localhost:";
    private static final String CONTROLLER_NAME = PriceController.REST_NAME;

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

    @Test
    public void eachOsHasDifferentPrice() throws Exception {
        Collection<Double> prices = Arrays.stream(OSType.values())
                .map(os -> new OrderUcp("t2.large", "Oregon", os))
                .map(HttpEntity::new)
                .map(order -> restTemplate.postForObject(HTTP_LOCALHOST + port + CONTROLLER_NAME, order, PriceEstimation.class))
                .map(PriceEstimation::getPrice)
                .collect(Collectors.toSet());
        assertThat(prices).hasSize(4);
    }
}