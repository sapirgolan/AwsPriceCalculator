package com.sap.ucp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderUcp {

    private int quntity;
    private String tShirtSize;
    private String region;
    private String os;

    public OrderUcp(String tShirtSize, String region) {
        this.quntity = 1;
        this.tShirtSize = tShirtSize;
        this.region = region;
    }

    public OrderUcp() {
    }

    public String gettShirtSize() {
        return tShirtSize;
    }

    public String getRegion() {
        return StringUtils.stripAccents(region);
    }

    public String getOs() {
        return os;
    }
}
