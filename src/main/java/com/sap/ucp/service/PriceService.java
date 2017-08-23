package com.sap.ucp.service;

import com.sap.ucp.model.Product;
import com.sap.ucp.parsers.JsonSteamDataSupplier;
import com.sap.ucp.parsers.ProductStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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
    public Map<String, Map<String, List<Product>>> initProducts() {
        InputStream productsStream = getClass().getClassLoader().getResourceAsStream("products_ec2.minify.json");
        JsonSteamDataSupplier<Product> supplier = new JsonSteamDataSupplier<>(productsStream, new ProductStrategy());
        Stream<Product> stream = supplier.getStream();
        return stream.filter(p -> StringUtils.equals("NA", p.getPreInstalledSw()))
                .collect(groupingBy(Product::getInstanceType,
                        groupingBy((Product product) -> {
                            String region = StringUtils.substringBetween(product.getLocation(), "(", ")");
                            return region != null ? region.toLowerCase(): region;
                        })));

    }
}
