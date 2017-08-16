package com.sap.ucp.parsers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.sap.ucp.model.PriceDimensions;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Created by i062070 on 17/08/2017.
 */
public class PriceDimensionDeserializer extends StdDeserializer<PriceDimensions> {
    private static ObjectMapper mapper = ObjectMapperSingleton.getInstance();

    public PriceDimensionDeserializer(Class<PriceDimensions> t) {
        super(t);
    }

    public PriceDimensionDeserializer() {
        this(null);
    }

    @Override
    public PriceDimensions deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        String fieldName = parser.nextFieldName();
        if (StringUtils.isNotEmpty(fieldName) && StringUtils.countMatches(fieldName, ".") == 2) {
            if (ParserUtility.isBeginningOfObject(parser.nextToken())) {
                TreeNode treeNode = parser.readValueAsTree();
                return mapper.convertValue(treeNode, PriceDimensions.class);
            }
        }
        return null;
    }

}
