package com.sap.ucp.service;

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

    @Mock /* in version 1.10.19 of mockito, it fails to inject 'products' to PriceService unless both members exists */
    private Map<String, Map<String, List<Product>>> products;

    @Mock /* in version 1.10.19 of mockito, it fails to inject 'products' to PriceService unless both members exists */
    private Map<String, List<Price>> prices;

    @Test
    public void computingInstancesAreGroupedByTshirtSize() throws Exception {
        priceService.initProducts();
        Map<String, Map<String, List<Product>>> products = priceService.getProducts();

        assertThat(products, Matchers.hasKey("t2.small"));
        assertThat(products, IsMapWithSize.aMapWithSize(94));
    }

    @Test
    public void computingInstancesHaveAllRegionsInLowerCase() throws Exception {
        priceService.initProducts();
        Map<String, Map<String, List<Product>>> products = priceService.getProducts();

        assertThat(products, Matchers.hasKey("t2.medium"));
        Map<String, List<Product>> productsByType = products.get("t2.medium");
        Set<String> allRegionsOfT2Medium = productsByType.keySet();

        Arrays.stream(ALL_REGIONS).map(String::toLowerCase).forEach(region -> assertThat(allRegionsOfT2Medium, Matchers.hasItem(region)));
    }

    @Test
    public void allPricesExist() throws Exception {
        priceService.initPrices();
        Map<String, List<Price>> prices = priceService.getPrices();
        assertThat(prices, IsMapWithSize.aMapWithSize(19007));
    }

    @Test
    public void calculatePriceForNullValue_returnErrorValue() throws Exception {
        assertThat(priceService.calculateHourlyPrice(null, "Frankfurt", 1), Matchers.closeTo(-1.0, 0.00000));
        assertThat(priceService.calculateHourlyPrice("t2.meduim", null, 1), Matchers.closeTo(-1.0, 0.00000));
    }

    @Test
    public void priceOfPriceObjectWithoutPrice_shouldBeNegative() throws Exception {
        String fakeSku = UUID.randomUUID().toString();
        Price mockPrice = Mockito.mock(Price.class);
        Whitebox.setInternalState(priceService, "prices", new HashMap<String, List<Price>>());
        priceService.getPrices().put(fakeSku, Arrays.asList(mockPrice));

        assertThat(priceService.getHourlyPrice(Arrays.asList(fakeSku)), closeTo(-1.0, 0.000));
    }

    @Test
    public void calculatePriceForInvalidTime_returnErrorValue() throws Exception {
        IntStream.rangeClosed(-3,0).
                forEach( hour -> assertThat(priceService.calculateHourlyPrice("someSize", "Frankfurt", hour),
                                    Matchers.closeTo(-1.0, 0.00000)));
    }

    @Test
    public void whenTShirtNotExists_returnEmptyCollection() throws Exception {
        Collection<String> skus = priceService.getProductSkus("fakeTShirstSize", null);
        assertThat(skus, emptyCollectionOf(String.class));
    }

    @Test
    public void whenTShirtNotExistsAtRegion_returnEmptyCollection() throws Exception {
        String tShirtSize = "fakeTShirstSize";
        when(products.containsKey(tShirtSize)).thenReturn(true);
        when(products.get(tShirtSize)).thenReturn(mock(Map.class));

        Collection<String> skus = priceService.getProductSkus(tShirtSize, "fakeRegion");
        assertThat(skus, emptyCollectionOf(String.class));
    }
}