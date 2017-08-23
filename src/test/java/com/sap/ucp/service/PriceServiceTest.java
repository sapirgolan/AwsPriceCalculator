package com.sap.ucp.service;

import com.sap.ucp.model.Product;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsMapWithSize;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertThat;

/**
 * Created by i062070 on 22/08/2017.
 */
public class PriceServiceTest {

    public static final String[] ALL_REGIONS = new String[]{"Oregon", "Frankfurt", "N. Virginia", "Singapore", "Central", "Sao Paulo", "Mumbai", "Seoul", "Tokyo", "Ireland", "London", "N. California", "Ohio", "US", "Sydney"};

    @Test
    public void computingInstancesAreGroupedByTshirtSize() throws Exception {
        PriceService priceService = new PriceService();
        Map<String, Map<String, List<Product>>> products = priceService.initProducts();
        assertThat(products, Matchers.hasKey("t2.small"));
        assertThat(products, IsMapWithSize.aMapWithSize(94));
    }

    @Test
    public void computingInstancesHaveAllRegionsInLowerCase() throws Exception {
        PriceService priceService = new PriceService();
        Map<String, Map<String, List<Product>>> products = priceService.initProducts();
        assertThat(products, Matchers.hasKey("t2.medium"));
        Map<String, List<Product>> productsByType = products.get("t2.medium");
        Set<String> allRegionsOfT2Medium = productsByType.keySet();

        Arrays.stream(ALL_REGIONS).map(String::toLowerCase).forEach(region -> assertThat(allRegionsOfT2Medium, Matchers.hasItem(region)));
    }
}