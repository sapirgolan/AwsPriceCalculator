package com.sap.ucp.service;

import com.google.common.collect.ImmutableMap;
import com.sap.ucp.model.OrderUcp;
import com.sap.ucp.model.Product;
import com.sap.ucp.types.OSType;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsMapWithSize;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PriceService.class)
public class PriceServiceContextTest {

    @Autowired
    PriceService priceService;

    @Test
    public void initProductsStartsAfterSpringContextIsLoaded() throws Exception {
        assertThat(priceService.getProductsMap(), IsMapWithSize.aMapWithSize(94));
    }

    @Test
    public void initPricesStartAfterSpringContextIsLoaded() throws Exception {
        assertThat(priceService.getPricesMap(), IsMapWithSize.aMapWithSize(19007));
    }

    @Test
    @DirtiesContext
    public void priceOfNonExistingSku_shouldBeNegative() throws Exception {
        String fakeSku = UUID.randomUUID().toString();
        Product mockProduct = Mockito.mock(Product.class);

        priceService.getProductsMap().put(fakeSku, ImmutableMap.of("Frankfurt", Arrays.asList(mockProduct)));
        OrderUcp order = new OrderUcp(fakeSku, "Frankfurt");
        assertThat(priceService.calculateHourlyPrice(order, 1), Matchers.closeTo(-1.0, 0.00000));

        Mockito.when(mockProduct.getSku()).thenReturn("fakeSku");
        assertThat(priceService.calculateHourlyPrice(order, 1), Matchers.closeTo(-1.0, 0.00000));
    }

    @Test
    public void priceOf_F73WDC2MSMN85Z9Z_is_16Dot8060000000() throws Exception {
        assertThat(priceService.getHourlyPrice(Arrays.asList("F73WDC2MSMN85Z9Z")), closeTo(16.8060000000, 0.00001));
    }

    @Test
    public void calculateHourlyPriceForNonExistingProduct_returnErrorValue() throws Exception {
        OrderUcp nonExistingTShirtSize = new OrderUcp("nonExistingTShirtSize", "Frankfurt");
        OrderUcp newDataCenter = new OrderUcp("d2.xlarge", "new DataCenter");
        assertThat(priceService.calculateHourlyPrice(nonExistingTShirtSize), Matchers.closeTo(-1.0, 0.00000));
        assertThat(priceService.calculateHourlyPrice(newDataCenter), Matchers.closeTo(-1.0, 0.00000));
    }

    @Test
    public void priceOf_T2Large_Oregon_DefaultOS_For24Hours_shouldBe() throws Exception {
        assertThat(priceService.calculateHourlyPrice(new OrderUcp("t2.large", "Oregon"), 24), Matchers.closeTo(4.656, 0.000001));
    }

    @Test
    public void eachOsHasDifferentPrice() throws Exception {
        Collection<Double> prices = Arrays.stream(OSType.values())
                .map(os -> new OrderUcp("t2.large", "Oregon", os))
                .map(priceService::calculateHourlyPrice)
                .collect(Collectors.toSet());
        assertThat(prices, hasSize(4));
    }
}