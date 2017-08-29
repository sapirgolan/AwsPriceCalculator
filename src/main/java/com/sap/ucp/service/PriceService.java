package com.sap.ucp.service;

import com.sap.ucp.model.Price;
import com.sap.ucp.model.Product;
import com.sap.ucp.parsers.JsonSteamDataSupplier;
import com.sap.ucp.parsers.strategy.JsonStrategy;
import com.sap.ucp.parsers.strategy.PriceStrategy;
import com.sap.ucp.parsers.strategy.ProductStrategy;
import com.sap.ucp.validator.PriceValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Created by i062070 on 22/08/2017.
 */
@Service
public class PriceService {

    public static final String NO_INSTALLED_SOFTWARE = "NA";
    public static final String PRODUCTS_EC2_FILE_NAME = "products_ec2.minify.json";
    public static final String TERMS_EC2_FILE_NAME = "terms_ec2.minify.json";
    public static final double ERROR_PRICE = -1.0;
    public static final List<String> EMPTY_STRING_LIST = Collections.emptyList();
    private Map<String, Map<String, List<Product>>> products;
    private Map<String, List<Price>> prices;
    private final Logger logger = LoggerFactory.getLogger(PriceService.class);


    protected Map<String, Map<String, List<Product>>> getProducts() {
        return products;
    }

    protected Map<String, List<Price>> getPrices() {
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

    public double calculateHourlyPrice(String tShirtSize, String region) {
        return calculateHourlyPrice(tShirtSize, region, 1);
    }

    public double calculateHourlyPrice(String tShirtSize, String region, int hours) {
        if (!PriceValidator.isValid(tShirtSize, region, hours)) return ERROR_PRICE;

        Collection<String> skus = getProductSkus(tShirtSize, region);
        if (CollectionUtils.isEmpty(skus))
            return ERROR_PRICE;
        return hours * getHourlyPrice(skus);
    }

    protected Collection<String> getProductSkus(String tShirtSize, String region) {
        if (!products.containsKey(tShirtSize)) {
            logger.warn(String.format("There is no product with TShirt size '%s'", tShirtSize));
            return EMPTY_STRING_LIST;
        }
        List<Product> products = this.products.get(tShirtSize).get(region.toLowerCase());
        if (CollectionUtils.isEmpty(products)) {
            logger.warn(String.format("There is no product with TShirt size '%s' at region '%s'", tShirtSize, region));
            return EMPTY_STRING_LIST;
        }
        return products.stream().map(Product::getSku).collect(toList());
    }

    protected double getHourlyPrice(Collection<String> skus) {
        List<String> existingSkus = skus.stream().filter(s -> prices.containsKey(s)).collect(toList());
        if (CollectionUtils.isEmpty(existingSkus))
            return ERROR_PRICE;
        OptionalDouble max = existingSkus.stream().map(prices::get)
                .filter(p -> p != null)
                .map(l -> l.get(0))
                .mapToDouble(Price::getPrice)
                .max();
        double returnedPrice = max.orElse(ERROR_PRICE);
        if (returnedPrice <= 0) {
            logger.warn(String.format("Hourly price of product with SKU '%s' is not positive (%s)", skus, returnedPrice));
            returnedPrice = ERROR_PRICE;
        }
        return  returnedPrice;
    }
}
