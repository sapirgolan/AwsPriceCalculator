package com.sap.ucp.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created by i062070 on 13/08/2017.
 */
public class JsonSteamDataSupplier<T> implements Iterator<T> {

    private final Class<T> type;
    private boolean hasNext = false;
    private final Logger logger = LoggerFactory.getLogger(JsonSteamDataSupplier.class);
    JsonFactory jsonFactory = new JsonFactory();
    static ObjectMapper mapper = new ObjectMapper();
    private JsonParser parser;


    public JsonSteamDataSupplier(InputStream stream, Class<T> type) {
        this.type = type;

        if (isStreamNotAvailable(stream)) {
            return;
        }
        try {
            initParser(stream);
            if (!validateBeginningOfObject())
                return;

            hasNext = searchForParentByName("products");
        } catch (IOException e) {
            logger.error("Failed to create a parser for inputStream");
        }
    }

    private boolean searchForParentByName(String parent) throws IOException {
        boolean hasNext = false;
        JsonToken nextToken = parser.nextToken();
        while (nextToken != JsonToken.END_OBJECT) {
            if (nextToken == JsonToken.FIELD_NAME) {
                String filedName = parser.getCurrentName();
                if (StringUtils.equals(filedName, parent)) {
                    return navigateToFirstPbjectInParent();
                }else {
                    nextToken = skipToNextToken();
                }
            } else {
                nextToken = skipToNextToken();
            }
        }
        return hasNext;
    }

    private boolean navigateToFirstPbjectInParent() throws IOException {
        if (!JsonToken.START_OBJECT.equals(parser.nextToken()))
            return false;
        return true;
    }

    private JsonToken skipToNextToken() throws IOException {
        parser.skipChildren();
        return parser.nextToken();
    }

    private void initParser(InputStream stream) throws IOException {
        parser = jsonFactory.createParser(stream);
        parser.setCodec(mapper);
    }

    private boolean validateBeginningOfObject() {
        boolean validation = false;
        try {
            JsonToken token = parser.nextToken();
            validation = JsonToken.START_OBJECT.equals(token);
        } catch (IOException e) {
            logger.error("Failed to parse 1'st token on JSON");
        }
        return validation;
    }

    private boolean isStreamNotAvailable(InputStream stream) {
        try {
            return stream == null || stream.available() < 1;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean hasNext() {
        if (!hasNext)
            return false;
        try {
            if (!JsonToken.FIELD_NAME.equals(parser.nextToken()))
                return false;
            JsonToken token = parser.nextToken();
            return JsonToken.START_OBJECT.equals(token);
        } catch (IOException e) {
            logger.error("Failed to get next token.", e);
            return false;
        }
    }

    @Override
    public T next() {
        try {
            TreeNode treeNode = parser.readValueAsTree();
            return treeNode == null ? null : mapper.convertValue(treeNode, type);
        } catch (IOException e) {
            logger.error("Failed ", e);
        }
        return null;
    }
}
