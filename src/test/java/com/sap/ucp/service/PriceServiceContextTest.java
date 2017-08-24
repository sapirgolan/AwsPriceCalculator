package com.sap.ucp.service;

import com.google.common.collect.ImmutableMap;
import com.sap.ucp.model.Product;
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
import java.util.UUID;

import static org.hamcrest.Matchers.closeTo;
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

    @Test
    @DirtiesContext
    public void priceOfNonExistingSku_shouldBeNegative() throws Exception {
        String fakeSku = UUID.randomUUID().toString();
        Product mockProduct = Mockito.mock(Product.class);

        priceService.getProducts().put(fakeSku, ImmutableMap.of("Frankfurt", Arrays.asList(mockProduct)));
        assertThat(priceService.calculateHourlyPrice(fakeSku, "Frankfurt", 1), Matchers.closeTo(-1.0, 0.00000));

        Mockito.when(mockProduct.getSku()).thenReturn("fakeSku");
        assertThat(priceService.calculateHourlyPrice(fakeSku, "Frankfurt", 1), Matchers.closeTo(-1.0, 0.00000));
    }

    @Test
    public void priceOf_F73WDC2MSMN85Z9Z_is_16Dot8060000000() throws Exception {
        assertThat(priceService.getHourlyPrice("F73WDC2MSMN85Z9Z"), closeTo(16.8060000000, 0.00001));
    }

    @Test
    public void calculateHourlyPriceForNonExistingProduct_returnErrorValue() throws Exception {
        assertThat(priceService.calculateHourlyPrice("nonExistingTShirtSize", "Frankfurt"), Matchers.closeTo(-1.0, 0.00000));
        assertThat(priceService.calculateHourlyPrice("d2.xlarge", "new DataCenter"), Matchers.closeTo(-1.0, 0.00000));
    }

    @Test
    public void priceOfT2LargeInOregonFor24Hours_shouldBe() throws Exception {
        assertThat(priceService.calculateHourlyPrice("t2.large", "Oregon", 24), Matchers.closeTo(3.696, 0.000001));
    }
}