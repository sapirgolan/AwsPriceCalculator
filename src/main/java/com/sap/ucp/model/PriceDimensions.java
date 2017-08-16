package com.sap.ucp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by i062070 on 17/08/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "priceDimensions")
public class PriceDimensions {
    private String description;
    private String unit;
    @JsonProperty("pricePerUnit")
    private PricePerUnit price;

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }

    public double getPrice() {
        return price.getPrice();
    }

    public class PricePerUnit {
        @JsonProperty("USD")
        private double price;

        public double getPrice() {
            return price;
        }
    }

    public PriceDimensions() {
        this.description = StringUtils.EMPTY;
        this.unit = StringUtils.EMPTY;
        this.price = new PricePerUnit();
    }
}
