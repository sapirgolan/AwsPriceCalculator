package com.sap.ucp.parsers.strategy;

import com.fasterxml.jackson.core.JsonParser;
import com.sap.ucp.model.Price;
import com.sap.ucp.parsers.ParserUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by i062070 on 16/08/2017.
 */
public class PriceStrategy implements JsonStrategy<Price> {
    private final Logger logger = LoggerFactory.getLogger(PriceStrategy.class);

    @Override
    public Class<Price> getType() {
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
        return managedToStepIntoObject(parser);
    }

    /**
     * When one of the values of a JSONObject is a JSONObject itself, you need
     * to traverse the JSON in order to enter that object
     * @param parser
     * @return
     * @throws IOException
     */
    private boolean managedToStepIntoObject(JsonParser parser) throws IOException {
        if (!ParserUtility.isFieldName(parser.nextToken())){
            return false;
        }
        return ParserUtility.isBeginningOfObject(parser.nextToken());
    }

    @Override
    public void performActionAfterReadValue(JsonParser parser) {
        try {
            parser.nextToken();
        } catch (IOException e) {
            logger.error("Failed to perform read operation of toekn", e);
        }
    }

    /**
     * The Price JSONObject in AWS JSON file contains wrappers that are not relevant for us.
     * Therefore, we step into this wrappers until we reach the actual Price Object
     * <p>
     *      "MMWYT6C5AE7DJWT7": {
                "MMWYT6C5AE7DJWT7.JRTCKXETXF": {
                    "sku": "MMWYT6C5AE7DJWT7"...
                }
            }
     * </p>
     * @param parser
     * @return
     * @throws IOException
     */
    @Override
    public boolean hasNext(JsonParser parser) throws IOException {
        for (int i = 0; i < 2; i++) {
            if (!managedToStepIntoObject(parser))
                return false;
        }
        return true;
    }
}
