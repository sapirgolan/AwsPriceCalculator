package com.sap.ucp.validator;

import org.apache.commons.lang3.StringUtils;

public class PriceValidator {
    public static boolean isValid(String tShirtSize, String region, int hours) {
        if (StringUtils.isAnyEmpty(tShirtSize, region))
            return false;
        return hours > 0;
    }
}
