package com.sap.ucp.service;

import com.sap.ucp.model.Price;
import com.sap.ucp.model.Product;
import com.sap.ucp.parsers.JsonSteamDataSupplier;
import com.sap.ucp.parsers.strategy.JsonStrategy;
import com.sap.ucp.parsers.strategy.PriceStrategy;
import com.sap.ucp.parsers.strategy.ProductStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

/**
 * Created by i062070 on 22/08/2017.
 */
@Service
public class PriceService {

    public static final String NO_INSTALLED_SOFTWARE = "NA";
    public static final String PRODUCTS_EC2_FILE_NAME = "products_ec2.minify.json";
    public static final String TERMS_EC2_FILE_NAME = "terms_ec2.minify.json";
    private Map<String, Map<String, List<Product>>> products;
    private Map<String, List<Price>> prices;

    public Map<String, Map<String, List<Product>>> getProducts() {
        return products;
    }

    public Map<String, List<Price>> getPrices() {
        return prices;
    }

    @PostConstruct
    protected void initProducts() {
        Stream<Product> stream = getStreamFromFile(new ProductStrategy(), PRODUCTS_EC2_FILE_NAME);

        products = stream.filter(p -> StringUtils.equals(NO_INSTALLED_SOFTWARE, p.getPreInstalledSw()))
                .collect(groupingBy(Product::getInstanceType,
                        groupingBy((Product product) -> {
                            String region = StringUtils.substringBetween(product.getLocation(), "(", ")");
                            return region != null ? region.toLowerCase() : region;
                        })));
    }

    @PostConstruct
    protected void initPrices() {
        Stream<Price> stream = getStreamFromFile(new PriceStrategy(), TERMS_EC2_FILE_NAME);

        prices = stream.collect(groupingBy(Price::getSku));
    }

    private <T> Stream<T> getStreamFromFile(JsonStrategy<T> strategy, String jsonFileName) {
        InputStream productsStream = getClass().getClassLoader().getResourceAsStream(jsonFileName);
        JsonSteamDataSupplier<T> supplier = new JsonSteamDataSupplier<>(productsStream, strategy);
        return supplier.getStream();
    }
}
