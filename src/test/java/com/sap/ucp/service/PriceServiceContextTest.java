package com.sap.ucp.service;

import org.hamcrest.collection.IsMapWithSize;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PriceService.class)
public class PriceServiceContextTest {

    @Autowired
    PriceService priceService;

    @Test
    public void initProductsStartsOnBeanInit() throws Exception {
        assertThat(priceService.getProducts(), IsMapWithSize.aMapWithSize(94));
    }

}