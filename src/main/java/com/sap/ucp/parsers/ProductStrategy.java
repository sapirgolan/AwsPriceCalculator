package com.sap.ucp.parsers;

import com.fasterxml.jackson.core.JsonParser;
import com.sap.ucp.model.Product;

import java.io.IOException;

/**
 * Created by i062070 on 16/08/2017.
 */
public class ProductStrategy implements JsonStrategy{
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
        if (!ParserUtility.isBeginningOfObject(parser.nextToken()))
            return false;
        return true;
    }
}
