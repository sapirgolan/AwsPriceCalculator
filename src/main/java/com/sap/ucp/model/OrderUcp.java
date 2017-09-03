package com.sap.ucp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderUcp {

    private int quntity;
    private String tShirtSize;

    public OrderUcp(String tShirtSize) {
        this.quntity = 1;
        this.tShirtSize = tShirtSize;
    }

    public OrderUcp() {
    }

    public String gettShirtSize() {
        return tShirtSize;
    }
}
