package com.sap.ucp.parsers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.sap.ucp.parsers.strategy.JsonStrategy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by i062070 on 13/08/2017.
 */
public class JsonSteamDataSupplier<T> implements Iterator<T> {

    private final JsonStrategy<T> strategy;
    private boolean hasNext = false;
    private final Logger logger = LoggerFactory.getLogger(JsonSteamDataSupplier.class);
    JsonFactory jsonFactory = new JsonFactory();
    static ObjectMapper mapper = ObjectMapperSingleton.getInstance();
    private JsonParser parser;

    public JsonSteamDataSupplier(InputStream stream, JsonStrategy<T> strategy) {
        this.strategy = strategy;

        if (isStreamNotAvailable(stream)) {
            return;
        }
        try {
            initParser(stream);
            if (!validateBeginningOfObject())
                return;

            hasNext = searchForParentByName();
            logger.debug("After searching for 1'st element using " + strategy.getClass().getSimpleName() + " hasNext==" + hasNext);
        } catch (IOException e) {
            logger.error("Failed to create a parser for inputStream");
        }
    }

    public Stream<T> getStream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, 0),false);
    }

    public Stream<T> getParallelStream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, 0),true);
    }

    @Override
    public boolean hasNext() {
        try {
            return hasNext && strategy.hasNext(parser);
        } catch (IOException e) {
            logger.error("Failed to retrieve next token and therefore failed to check if more elements exist.", e);
        }
        return false;
    }

    @Override
    public T next() {
        try {
            TreeNode treeNode = parser.readValueAsTree();
            if (treeNode == null || treeNode == NullNode.getInstance())
                return null;
            else {
                T t = mapper.convertValue(treeNode, strategy.getType());
                strategy.performActionAfterReadValue(parser);
                return t;
            }
        } catch (IOException e) {
            logger.error("Failed ", e);
        }
        return null;
    }

    private boolean searchForParentByName() throws IOException {
        logger.debug("Starting to search for 1'st parent in JSON");
        JsonToken nextToken = parser.nextToken();
        while (nextToken != JsonToken.END_OBJECT) {
            if (ParserUtility.isFieldName(nextToken)) {
                String filedName = parser.getCurrentName();
                if (StringUtils.equals(filedName, strategy.getRootFieldName())) {
                    return strategy.navigateToFirstObjectInParent(parser);
                }else {
                    nextToken = skipToNextToken();
                }
            } else {
                nextToken = skipToNextToken();
            }
        }
        return false;
    }

    private JsonToken skipToNextToken() throws IOException {
        parser.skipChildren();
        return parser.nextToken();
    }

    private void initParser(InputStream stream) throws IOException {
        logger.debug("init parser");
        parser = jsonFactory.createParser(stream);
        parser.setCodec(mapper);
    }

    private boolean validateBeginningOfObject() {
        boolean validation = false;
        try {
            JsonToken token = parser.nextToken();
            validation = ParserUtility.isBeginningOfObject(token);
        } catch (IOException e) {
            logger.error("Failed to parse 1'st token on JSON");
        }
        return validation;
    }

    private boolean isStreamNotAvailable(InputStream stream) {
        try {
            logger.debug("Stream is empty or null");
            return stream == null || stream.available() < 1;
        } catch (IOException e) {
            return false;
        }
    }

}
