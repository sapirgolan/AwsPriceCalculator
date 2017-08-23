package com.sap.ucp.parsers.strategy;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.sap.ucp.model.Product;
import com.sap.ucp.parsers.ParserUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by i062070 on 16/08/2017.
 */
public class ProductStrategy implements JsonStrategy<Product> {
    private final Logger logger = LoggerFactory.getLogger(ProductStrategy.class);

    @Override
    public Class<Product> getType() {
        return Product.class;
    }

    @Override
    public String getRootFieldName() {
        return "products";
    }

    @Override
    public boolean navigateToFirstObjectInParent(JsonParser parser) throws IOException {
        return ParserUtility.isBeginningOfObject(parser.nextToken());
    }

    @Override
    public boolean hasNext(JsonParser parser) throws IOException {
        JsonToken token = parser.nextToken();
        if (!ParserUtility.isFieldName(token))
            return false;
        token = parser.nextToken();
        return ParserUtility.isBeginningOfObject(token);
    }
}
