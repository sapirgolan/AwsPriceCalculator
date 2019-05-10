package com.sap.ucp.service;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.sap.ucp.model.OrderUcp;
import com.sap.ucp.model.Product;
import com.sap.ucp.types.OSType;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PriceService.class)
public class PriceServiceContextTest {

    private static final String FRANKFURT = "Frankfurt";
    @Autowired
    PriceService priceService;

    @Test
    public void initProductsStartsAfterSpringContextIsLoaded() {
        assertThat(priceService.getProductsMap()).hasSize(94);
    }

    @Test
    public void initPricesStartAfterSpringContextIsLoaded() {
        assertThat(priceService.getPricesMap()).hasSize(19007);
    }

    @Test
    @DirtiesContext
    public void priceOfNonExistingSku_shouldBeNegative() {
        String fakeSku = UUID.randomUUID().toString();
        Product mockProduct = Mockito.mock(Product.class);

        priceService.getProductsMap().put(fakeSku, ImmutableMap.of(FRANKFURT, singletonList(mockProduct)));
        OrderUcp order = new OrderUcp(fakeSku, FRANKFURT);
        assertThat(priceService.calculateHourlyPrice(order, 1))
            .isCloseTo(-1.0, Offset.offset(0.00000));

        Mockito.when(mockProduct.getSku()).thenReturn("fakeSku");
        assertThat(priceService.calculateHourlyPrice(order, 1))
            .isCloseTo(-1.0, Offset.offset(0.00000));
    }

    @Test
    public void priceOf_F73WDC2MSMN85Z9Z_is_16Dot8060000000() {
        assertThat(priceService.getHourlyPrice(singletonList("F73WDC2MSMN85Z9Z")))
            .isCloseTo(16.8060000000, Offset.offset(0.00001));
    }

    @Test
    public void calculateHourlyPriceForNonExistingProduct_returnErrorValue() {
        OrderUcp nonExistingTShirtSize = new OrderUcp("nonExistingTShirtSize", FRANKFURT);
        OrderUcp newDataCenter = new OrderUcp("d2.xlarge", "new DataCenter");
        assertThat(priceService.calculateHourlyPrice(nonExistingTShirtSize))
            .isCloseTo(-1.0, Offset.offset(0.00000));
        assertThat(priceService.calculateHourlyPrice(newDataCenter))
            .isCloseTo(-1.0, Offset.offset(0.00000));
    }

    @Test
    public void priceOf_T2Large_Oregon_DefaultOS_For24Hours_shouldBe() {
        assertThat(priceService.calculateHourlyPrice(new OrderUcp("t2.large", "Oregon"), 24))
            .isCloseTo(4.656, Offset.offset(0.00001));
    }

    @Test
    public void eachOsHasDifferentPrice() {
        Collection<Double> prices = Arrays.stream(OSType.values())
                .map(os -> new OrderUcp("t2.large", "Oregon", os))
                .map(priceService::calculateHourlyPrice)
                .collect(Collectors.toSet());
        assertThat(prices).hasSize(4);
    }
}