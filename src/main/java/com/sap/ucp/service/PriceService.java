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
    public static final double ERROR_PRICE = -1.0;
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

        String sku = getProductSku(tShirtSize, region);
        logger.warn(String.format("There is no product with TShirt size '%s' at region '%s'", tShirtSize, region));
        if (StringUtils.EMPTY.equals(sku))
            return ERROR_PRICE;
        return hours * getHourlyPrice(sku);
    }

    protected String getProductSku(String tShirtSize, String region) {
        if (!products.containsKey(tShirtSize)) {
            return StringUtils.EMPTY;
        }
        List<Product> products = this.products.get(tShirtSize).get(region.toLowerCase());
        if (CollectionUtils.isEmpty(products))
            return StringUtils.EMPTY;
        return products.get(0).getSku();
    }

    protected double getHourlyPrice(String sku) {
        if (!prices.containsKey(sku))
            return ERROR_PRICE;
        Price price = prices.get(sku).get(0);
        double returnedPrice = price.getPrice();
        if (returnedPrice <= 0) {
            logger.warn(String.format("Hourly price of product with SKU '%s' is not positive (%s)", sku, returnedPrice));
            returnedPrice = ERROR_PRICE;
        }
        return  returnedPrice;
    }
}
