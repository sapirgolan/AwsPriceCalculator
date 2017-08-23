package com.sap.ucp.service;

import com.sap.ucp.model.Price;
import com.sap.ucp.model.Product;
import com.sap.ucp.parsers.JsonSteamDataSupplier;
import com.sap.ucp.parsers.PriceStrategy;
import com.sap.ucp.parsers.ProductStrategy;
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
    private Map<String, Map<String, List<Product>>> products;

    @PostConstruct
    protected void initProducts() {
        InputStream productsStream = getClass().getClassLoader().getResourceAsStream("products_ec2.minify.json");
        JsonSteamDataSupplier<Product> supplier = new JsonSteamDataSupplier<>(productsStream, new ProductStrategy());
        Stream<Product> stream = supplier.getStream();
        products = stream.filter(p -> StringUtils.equals(NO_INSTALLED_SOFTWARE, p.getPreInstalledSw()))
                .collect(groupingBy(Product::getInstanceType,
                        groupingBy((Product product) -> {
                            String region = StringUtils.substringBetween(product.getLocation(), "(", ")");
                            return region != null ? region.toLowerCase() : region;
                        })));
    }

    public Map<String, Map<String, List<Product>>> getProducts() {
        return products;
    }

    public Map<String, List<Price>> initPrices() {
        InputStream termsStream = getClass().getClassLoader().getResourceAsStream("terms_ec2.minify.json");
        JsonSteamDataSupplier<Price> supplier = new JsonSteamDataSupplier<>(termsStream, new PriceStrategy());
        Stream<Price> stream = supplier.getStream();

        return stream.collect(groupingBy(Price::getSku));
    }
}
