package com.sap.ucp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by i062070 on 13/08/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    private String sku;
    private String productFamily;

    @JsonProperty("attributes")
    private ProductAttributes attributes;

    public String getSku() {
        return sku;
    }

    public String getProductFamily() {
        return productFamily;
    }

    public String getInstanceType() {
        if (attributes == null)
            return StringUtils.EMPTY;
        return attributes.getInstanceType();
    }

    public String getPreInstalledSw() {
        if (attributes == null)
            return StringUtils.EMPTY;
        return attributes.getPreInstalledSw();
    }

    public String getLocation() {
        if (attributes == null)
            return StringUtils.EMPTY;
        return attributes.getLocation();
    }
}
