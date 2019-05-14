package com.sap.ucp.service;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sap.ucp.model.OrderUcp;
import com.sap.ucp.model.Price;
import com.sap.ucp.model.Product;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by i062070 on 22/08/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class PriceServiceTest {

    private static final String[] ALL_REGIONS = new String[]{"Oregon", "Frankfurt", "N. Virginia", "Singapore", "Central", "Sao Paulo", "Mumbai", "Seoul", "Tokyo", "Ireland", "London", "N. California", "Ohio", "US", "Sydney"};

    @InjectMocks
    private PriceService priceService;

    @Mock /* in version 1.10.19 of mockito, it fails to inject 'productsMap' to PriceService unless both members exists */
    private Map<String, Map<String, List<Product>>> productsMap;

    @Mock /* in version 1.10.19 of mockito, it fails to inject 'productsMap' to PriceService unless both members exists */
    private Map<String, List<Price>> pricesMap;

    @Test
    public void computingInstancesAreGroupedByTshirtSize() {
        priceService.initProducts();
        Map<String, Map<String, List<Product>>> products = priceService.getProductsMap();


        assertThat(products).hasSize(94);
    }

    @Test
    public void computingInstancesHaveAllRegionsInLowerCase() {
        priceService.initProducts();
        Map<String, Map<String, List<Product>>> products = priceService.getProductsMap();

        assertThat(products).containsKey("t2.medium");
        Map<String, List<Product>> productsByType = products.get("t2.medium");
        Set<String> allRegionsOfT2Medium = productsByType.keySet();

        Arrays.stream(ALL_REGIONS)
            .map(String::toLowerCase)
            .forEach(region -> assertThat(allRegionsOfT2Medium).contains(region));
    }

    @Test
    public void allPricesExist() {
        priceService.initPrices();
        Map<String, Price> prices = priceService.getPricesMap();
        assertThat(prices).hasSize(19007);
    }

    @Test
    public void calculatePriceForNullValue_returnErrorValue() {
        assertThat(priceService.calculateHourlyPrice(new OrderUcp(null, "London"), 1))
            .isCloseTo(-1.0, Offset.offset(0.00000));
        assertThat(priceService.calculateHourlyPrice(new OrderUcp("t2.small", null), 1))
            .isCloseTo(-1.0, Offset.offset(0.00000));
        assertThat(priceService.calculateHourlyPrice(new OrderUcp("t2.small", null), 1))
            .isCloseTo(-1.0, Offset.offset(0.00000));

    }

    @Test
    public void priceOfPriceObjectWithoutPrice_shouldBeNegative() {
        String fakeSku = UUID.randomUUID().toString();
        Price mockPrice = Mockito.mock(Price.class);
        priceService.getPricesMap().put(fakeSku, mockPrice);

        assertThat(priceService.getHourlyPrice(singletonList(fakeSku)))
            .isCloseTo(-1.0, Offset.offset(0.00000));
    }

    @Test
    public void calculatePriceForInvalidTime_returnErrorValue() {
        IntStream.rangeClosed(-3,0).
                forEach(hour -> assertThat(priceService.calculateHourlyPrice(new OrderUcp("someSize", "london"), hour))
                    .isCloseTo(-1.0, Offset.offset(0.00000)));
    }

    @Test
    public void whenTShirtNotExists_returnEmptyCollection() {
        Collection<String> skus = priceService.getProductSkus(new OrderUcp("FakeSize", null));
        assertThat(skus).isEmpty();
    }

    @Test
    public void whenTShirtNotExistsAtRegion_returnEmptyCollection() {
        String tShirtSize = "fakeTShirstSize";
        when(productsMap.containsKey(tShirtSize)).thenReturn(true);

        Collection<String> skus = priceService.getProductSkus(new OrderUcp(tShirtSize, "fakeRegion"));
        assertThat(skus).isEmpty();
    }
}