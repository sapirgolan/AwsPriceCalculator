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

    public CurrencyRate(String base, String date, double rate) {
        this.base = base;
        this.date = date;
        this.rate = new Rate(rate);
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

        public Rate(double rate) {
            this.rate = rate;
        }

        public Rate() {
        }

        public double getExchangeRate() {
            return rate;
        }
    }
}
