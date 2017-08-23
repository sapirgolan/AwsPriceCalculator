package com.sap.ucp.parsers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.sap.ucp.model.Price;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by i062070 on 16/08/2017.
 */
public class PriceStrategy implements JsonStrategy {
    private final Logger logger = LoggerFactory.getLogger(PriceStrategy.class);

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
        if (!ParserUtility.isBeginningOfObject(parser.nextToken()))
            return false;
        if (!ParserUtility.isFieldName(parser.nextToken())){
            return false;
        }
        if (!ParserUtility.isBeginningOfObject(parser.nextToken())) {
            return false;
        }
        return true;
    }

    @Override
    public void performActionAfterReadValue(JsonParser parser) {
        try {
            parser.nextToken();
        } catch (IOException e) {
            logger.error("Failed to perform read operation of toekn", e);
        }
    }

    @Override
    public boolean hasNext(JsonParser parser) {
        try {
            for (int i = 0; i < 2; i++) {
                if (!ParserUtility.isFieldName(parser.nextToken()))
                    return false;
                if (!ParserUtility.isBeginningOfObject(parser.nextToken())){
                    return false;
                }
            }
        } catch (IOException e) {
            logger.error("Failed to get next token.", e);
            return false;
        }
        return true;
    }
}
