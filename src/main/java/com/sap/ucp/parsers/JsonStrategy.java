package com.sap.ucp.parsers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;

/**
 * Created by i062070 on 16/08/2017.
 */
public interface JsonStrategy<T> {
    Class<T> getType();

    String getRootFieldName();

    boolean navigateToFirstObjectInParent(JsonParser parser) throws IOException;

    default void performActionAfterReadValue(JsonParser parser) { }

    boolean hasNext(JsonParser parser) throws IOException;
}
