package com.sap.ucp.parsers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.sap.ucp.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by i062070 on 16/08/2017.
 */
public class ProductStrategy implements JsonStrategy{
    private final Logger logger = LoggerFactory.getLogger(ProductStrategy.class);

    @Override
    public Class getType() {
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
    public boolean hasNext(JsonParser parser) {
        try {
            JsonToken token = parser.nextToken();
            if (!ParserUtility.isFieldName(token))
                return false;
            token = parser.nextToken();
            return ParserUtility.isBeginningOfObject(token);
        } catch (IOException e) {
            logger.error("Failed to get next token.", e);
            return false;
        }
    }
}
