package com.sap.ucp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyRate {
    private String base;
    private String date;

    @JsonProperty("rates")
    private Rates rates;

    /**
     * KEEP THIS CONSTRUCTOR FOR JACKSON
     */
    public CurrencyRate() {
    }

    public CurrencyRate(String base, String date, double rates) {
        this.base = base;
        this.date = date;
        this.rates = new Rates(rates);
    }

    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    public double getRates() {
        return rates.getExchangeRate();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Rates {
        @JsonProperty("USD")
        private double rate;

        public Rates(double rate) {
            this.rate = rate;
        }

        public Rates() {
        }

        public double getExchangeRate() {
            return rate;
        }
    }
}
