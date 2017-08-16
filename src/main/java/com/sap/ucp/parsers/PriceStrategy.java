package com.sap.ucp.parsers;

import com.fasterxml.jackson.core.JsonParser;
import com.sap.ucp.model.Price;

import java.io.IOException;

/**
 * Created by i062070 on 16/08/2017.
 */
public class PriceStrategy implements JsonStrategy {
    @Override
    public Class getType() {
        return Price.class;
    }

    @Override
    public String getRootFieldName() {
        return "terms";
    }

    @Override
    public boolean navigateToFirstObjectInParent(JsonParser parser) throws IOException {
        for (int i = 0; i < 2; i++) {
            if (!ParserUtility.isBeginningOfObject(parser.nextToken()))
                return false;
            if (!ParserUtility.isFieldName(parser.nextToken())){
                return false;
            }
        }
        return ParserUtility.isBeginningOfObject(parser.nextToken());
    }
}
