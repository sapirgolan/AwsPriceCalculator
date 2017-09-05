package com.sap.ucp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderUcp {

    private int quntity;
    private String tShirtSize;
    private String region;

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
        return region;
    }
}
