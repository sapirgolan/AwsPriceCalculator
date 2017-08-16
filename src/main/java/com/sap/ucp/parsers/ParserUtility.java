package com.sap.ucp.parsers;

import com.fasterxml.jackson.core.JsonToken;

public class ParserUtility {

    public static boolean isBeginningOfObject(JsonToken token) {
        return JsonToken.START_OBJECT.equals(token);
    }

    public static boolean isFieldName(JsonToken token) {
        return JsonToken.FIELD_NAME.equals(token);
    }
}