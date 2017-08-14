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
            JsonToken nextToken = parser.nextToken();
            while (nextToken != JsonToken.END_OBJECT) {
                if (nextToken == JsonToken.FIELD_NAME) {
                    String filedName = parser.getCurrentName();
                    if (StringUtils.equals(filedName, "products")) {
                        hasNext = true;
                        return;
                    }else {
                        parser.skipChildren();
                        nextToken = parser.nextToken();
                    }
                } else {
                    parser.skipChildren();
                    nextToken = parser.nextToken();
                }
            }
            if (!hasNext)
                return;
        } catch (IOException e) {
            logger.error("Failed to create a parser for inputStream");
        }
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

    private void initObjectsStream(InputStream stream) {
        try {
            JsonToken token = parser.nextToken();
            if (token == null) {
                logger.error("Can't get any JSON Token from stream");
                hasNext = false;
                return;
            }
            if (!JsonToken.START_OBJECT.equals(token)) {
                logger.error("Can't get any JSON Token of Object start from");
                hasNext = false;
                return;
            }
        } catch (IOException e) {
            hasNext = false;
        }
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
            JsonToken token = parser.nextToken();
            return JsonToken.START_OBJECT.equals(token);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public T next() {
        try {
            TreeNode treeNode = parser.readValueAsTree();
            Iterator<String> stringIterator = treeNode.fieldNames();
            while (stringIterator.hasNext()) {
                String fieldName = stringIterator.next();
                TreeNode node = treeNode.get(fieldName);
                return mapper.convertValue(node, type);
            }
        } catch (IOException e) {
            logger.error("Failed ", e);
        }
        return null;
    }
}
