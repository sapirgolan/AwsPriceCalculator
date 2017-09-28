package com.sap.ucp.utils;

import com.sap.ucp.model.OrderUcp;
import com.sap.ucp.model.Product;
import com.sap.ucp.types.OSType;
import org.apache.commons.lang3.StringUtils;

public class OSUtil {
    public static boolean equal(Product product, OrderUcp order) {
        if (product == null || order == null)
            return false;
        boolean isSimpleCase = StringUtils.containsIgnoreCase(order.getOs(), product.getOS());
        boolean isLinux = OSType.Linux.toString().equals(product.getOS()) && StringUtils.containsIgnoreCase(order.getOs(), "Ubuntu");
        boolean isRedHat = OSType.RHEL.toString().equals(product.getOS()) && StringUtils.containsIgnoreCase(order.getOs(), "Red HAT Enterprise Linux");

        return isLinux || isRedHat || isSimpleCase;
    }
}
