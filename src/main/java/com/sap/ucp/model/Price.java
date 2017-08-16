package com.sap.ucp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.ucp.parsers.PriceDimensionDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Created by i062070 on 16/08/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Price {
    private String sku;
    private String effectiveDate;

    @JsonProperty("priceDimensions")
    @JsonDeserialize(using = PriceDimensionDeserializer.class)
    private Optional<PriceDimensions> priceDimensions = Optional.empty();

    public void setPriceDimensions(PriceDimensions priceDimensions) {
        this.priceDimensions = Optional.of(priceDimensions);
    }

    public String getSku() {
        return sku;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public String getDescription() {
        return priceDimensions.map(PriceDimensions::getDescription).orElse(StringUtils.EMPTY);
    }

    public String getUnit() {
        return priceDimensions.map(PriceDimensions::getUnit).orElse(StringUtils.EMPTY);
    }

    public double getPrice() {
        return priceDimensions.map(PriceDimensions::getPrice).orElse(0.0);
    }
}
