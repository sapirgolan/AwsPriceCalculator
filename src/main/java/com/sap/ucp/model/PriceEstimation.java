package com.sap.ucp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceEstimation {
    private double price;
    private String currency;

    public PriceEstimation() {
        currency = "EUR";
    }

    public PriceEstimation(double price) {
        this();
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }
}
