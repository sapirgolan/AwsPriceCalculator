package com.sap.ucp.validator;

import com.sap.ucp.model.OrderUcp;
import org.apache.commons.lang3.StringUtils;

public class OrderValidator {
    public static boolean isValid(OrderUcp order, int hours) {
        if (StringUtils.isAnyEmpty(order.gettShirtSize(), order.getRegion(), order.getOs()))
            return false;
        return hours > 0;
    }
}
