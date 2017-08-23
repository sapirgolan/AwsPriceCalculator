package com.sap.ucp.service;

import org.hamcrest.collection.IsMapWithSize;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PriceService.class)
public class PriceServiceContextTest {

    @Autowired
    PriceService priceService;

    @Test
    public void initProductsStartsAfterSpringContextIsLoaded() throws Exception {
        assertThat(priceService.getProducts(), IsMapWithSize.aMapWithSize(94));
    }

    @Test
    public void initPricesStartAfterSpringContextIsLoaded() throws Exception {
        assertThat(priceService.getPrices(), IsMapWithSize.aMapWithSize(19007));

    }
}