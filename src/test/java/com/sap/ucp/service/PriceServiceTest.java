package com.sap.ucp.service;

import com.sap.ucp.model.OrderUcp;
import com.sap.ucp.model.Price;
import com.sap.ucp.model.Product;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsMapWithSize;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.util.*;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by i062070 on 22/08/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class PriceServiceTest {

    public static final String[] ALL_REGIONS = new String[]{"Oregon", "Frankfurt", "N. Virginia", "Singapore", "Central", "Sao Paulo", "Mumbai", "Seoul", "Tokyo", "Ireland", "London", "N. California", "Ohio", "US", "Sydney"};

    @InjectMocks
    private PriceService priceService;

    @Mock /* in version 1.10.19 of mockito, it fails to inject 'productsMap' to PriceService unless both members exists */
    private Map<String, Map<String, List<Product>>> productsMap;

    @Mock /* in version 1.10.19 of mockito, it fails to inject 'productsMap' to PriceService unless both members exists */
    private Map<String, List<Price>> pricesMap;

    @Test
    public void computingInstancesAreGroupedByTshirtSize() throws Exception {
        priceService.initProducts();
        Map<String, Map<String, List<Product>>> products = priceService.getProductsMap();

        assertThat(products, Matchers.hasKey("t2.small"));
        assertThat(products, IsMapWithSize.aMapWithSize(94));
    }

    @Test
    public void computingInstancesHaveAllRegionsInLowerCase() throws Exception {
        priceService.initProducts();
        Map<String, Map<String, List<Product>>> products = priceService.getProductsMap();

        assertThat(products, Matchers.hasKey("t2.medium"));
        Map<String, List<Product>> productsByType = products.get("t2.medium");
        Set<String> allRegionsOfT2Medium = productsByType.keySet();

        Arrays.stream(ALL_REGIONS).map(String::toLowerCase).forEach(region -> assertThat(allRegionsOfT2Medium, Matchers.hasItem(region)));
    }

    @Test
    public void allPricesExist() throws Exception {
        priceService.initPrices();
        Map<String, Price> prices = priceService.getPricesMap();
        assertThat(prices, IsMapWithSize.aMapWithSize(19007));
    }

    @Test
    public void calculatePriceForNullValue_returnErrorValue() throws Exception {
        assertThat(priceService.calculateHourlyPrice(new OrderUcp(null, "London"), 1), Matchers.closeTo(-1.0, 0.00000));
        assertThat(priceService.calculateHourlyPrice(new OrderUcp("t2.small", null), 1), Matchers.closeTo(-1.0, 0.00000));
        assertThat(priceService.calculateHourlyPrice(new OrderUcp("t2.small", null), 1), Matchers.closeTo(-1.0, 0.00000));

    }

    @Test
    public void priceOfPriceObjectWithoutPrice_shouldBeNegative() throws Exception {
        String fakeSku = UUID.randomUUID().toString();
        Price mockPrice = Mockito.mock(Price.class);
        Whitebox.setInternalState(priceService, "pricesMap", new HashMap<String, List<Price>>());
        priceService.getPricesMap().put(fakeSku, mockPrice);

        assertThat(priceService.getHourlyPrice(Arrays.asList(fakeSku)), closeTo(-1.0, 0.000));
    }

    @Test
    public void calculatePriceForInvalidTime_returnErrorValue() throws Exception {
        IntStream.rangeClosed(-3,0).
                forEach(hour -> assertThat(priceService.calculateHourlyPrice(new OrderUcp("someSize", "london"), hour),
                                    Matchers.closeTo(-1.0, 0.00000)));
    }

    @Test
    public void whenTShirtNotExists_returnEmptyCollection() throws Exception {
        Collection<String> skus = priceService.getProductSkus(new OrderUcp("FakeSize", null));
        assertThat(skus, emptyCollectionOf(String.class));
    }

    @Test
    public void whenTShirtNotExistsAtRegion_returnEmptyCollection() throws Exception {
        String tShirtSize = "fakeTShirstSize";
        when(productsMap.containsKey(tShirtSize)).thenReturn(true);
        when(productsMap.get(tShirtSize)).thenReturn(mock(Map.class));

        Collection<String> skus = priceService.getProductSkus(new OrderUcp(tShirtSize, "fakeRegion"));
        assertThat(skus, emptyCollectionOf(String.class));
    }
}