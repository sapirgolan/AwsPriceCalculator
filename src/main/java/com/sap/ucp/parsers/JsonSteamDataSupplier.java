package com.sap.ucp.parsers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
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

    private final Class<T> type;
    private boolean hasNext = false;
    private final Logger logger = LoggerFactory.getLogger(JsonSteamDataSupplier.class);
    JsonFactory jsonFactory = new JsonFactory();
    static ObjectMapper mapper = ObjectMapperSingleton.getInstance();
    private JsonParser parser;

    public JsonSteamDataSupplier(InputStream stream, JsonStrategy strategy) {
        this.type = strategy.getType();

        if (isStreamNotAvailable(stream)) {
            return;
        }
        try {
            initParser(stream);
            if (!validateBeginningOfObject())
                return;

            hasNext = searchForParentByName(strategy);
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
        if (!hasNext)
            return false;
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

    @Override
    public T next() {
        try {
            TreeNode treeNode = parser.readValueAsTree();
            return treeNode == null || treeNode == NullNode.getInstance() ? null : mapper.convertValue(treeNode, type);
        } catch (IOException e) {
            logger.error("Failed ", e);
        }
        return null;
    }

    private boolean searchForParentByName(JsonStrategy strategy) throws IOException {
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
            return stream == null || stream.available() < 1;
        } catch (IOException e) {
            return false;
        }
    }

}
