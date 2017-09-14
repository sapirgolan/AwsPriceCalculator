package com.sap.ucp.service;

import com.sap.ucp.model.OrderUcp;
import com.sap.ucp.model.Price;
import com.sap.ucp.model.Product;
import com.sap.ucp.parsers.JsonSteamDataSupplier;
import com.sap.ucp.parsers.strategy.JsonStrategy;
import com.sap.ucp.parsers.strategy.PriceStrategy;
import com.sap.ucp.parsers.strategy.ProductStrategy;
import com.sap.ucp.validator.OrderValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
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
    private Map<String, Map<String, List<Product>>> productsMap;
    private Map<String, Price> pricesMap;
    private final Logger logger = LoggerFactory.getLogger(PriceService.class);

    protected Map<String, Map<String, List<Product>>> getProductsMap() {
        return productsMap;
    }

    protected Map<String, Price> getPricesMap() {
        return pricesMap;
    }

    @PostConstruct
    protected void initProducts() {
        logger.debug("Starting retrieving products from DISK");
        Stream<Product> stream = getStreamFromFile(new ProductStrategy(), PRODUCTS_EC2_FILE_NAME);

        try {
            productsMap = stream.filter(p -> StringUtils.equals(NO_INSTALLED_SOFTWARE, p.getPreInstalledSw()))
                    .collect(groupingBy(Product::getInstanceType,
                            groupingBy((Product product) -> {
                                String region = StringUtils.substringBetween(product.getLocation(), "(", ")");
                                return region != null ? region.toLowerCase() : region;
                            })));
        } catch (Throwable e) {
            logger.error("Failed to process products", e);
        }
        logger.debug("Finished retrieving products from DISK");
    }

    @PostConstruct
    protected void initPrices() {
        logger.debug("Starting retrieving prices from DISK");
        Stream<Price> stream = getStreamFromFile(new PriceStrategy(), TERMS_EC2_FILE_NAME);

        pricesMap = stream.collect(Collectors.toMap(Price::getSku, (Price p) -> p));
        logger.debug("Finished retrieving prices from DISK");
    }

    private <T> Stream<T> getStreamFromFile(JsonStrategy<T> strategy, String jsonFileName) {
        logger.debug("Starting to access resource from disk " + jsonFileName);
        InputStream productsStream = getClass().getClassLoader().getResourceAsStream(jsonFileName);
        logger.debug("got stream to " + jsonFileName + " isNull? " + (null == productsStream));
        JsonSteamDataSupplier<T> supplier = new JsonSteamDataSupplier<>(productsStream, strategy);
        logger.debug("Finished accessing resource from disk " + jsonFileName);
        return supplier.getStream();
    }

    public double calculateHourlyPrice(OrderUcp order) {
        return calculateHourlyPrice(order, 1);
    }

    public double calculateHourlyPrice(OrderUcp order, int hours) {
        if (!OrderValidator.isValid(order, hours)) return ERROR_PRICE;

        Collection<String> skus = getProductSkus(order);
        if (CollectionUtils.isEmpty(skus))
            return ERROR_PRICE;
        return hours * getHourlyPrice(skus);
    }

    protected Collection<String> getProductSkus(OrderUcp order) {
        String tShirtSize = order.gettShirtSize();
        if (!productsMap.containsKey(tShirtSize)) {
            logger.warn(String.format("There is no product with TShirt size '%s'", tShirtSize));
            return EMPTY_STRING_LIST;
        }
        List<Product> products = this.productsMap.get(tShirtSize).get(order.getRegion().toLowerCase());
        if (CollectionUtils.isEmpty(products)) {
            logger.warn(String.format("There is no product with TShirt size '%s' at region '%s'", tShirtSize, order.getRegion()));
            return EMPTY_STRING_LIST;
        }
        return products.stream().map(Product::getSku).collect(toList());
    }

    protected double getHourlyPrice(Collection<String> skus) {
        List<String> existingSkus = skus.stream().filter(s -> pricesMap.containsKey(s)).collect(toList());
        if (CollectionUtils.isEmpty(existingSkus))
            return ERROR_PRICE;
        OptionalDouble max = existingSkus.stream()
                .map(pricesMap::get)
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
