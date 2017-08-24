package com.sap.ucp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyRate {
    private String base;
    private String date;

    @JsonProperty("rates")
    private Rate rate;

    /**
     * KEEP THIS CONSTRUCTOR FOR JACKSON
     */
    public CurrencyRate() {
    }

    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    public double getRate() {
        return rate.getExchangeRate();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Rate {
        @JsonProperty("EUR")
        private double rate;

        public double getExchangeRate() {
            return rate;
        }
    }
}
