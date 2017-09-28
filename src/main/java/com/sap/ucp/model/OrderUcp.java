package com.sap.ucp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sap.ucp.types.OSType;
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

    public OrderUcp(String tShirt, String region, OSType os) {
        this(tShirt, region, os.toString());
    }

    public OrderUcp(String tShirtSize, String region, String os) {
        this(tShirtSize, region);
        this.os = os;
    }

    public String gettShirtSize() {
        return tShirtSize;
    }

    public String getRegion() {
        return StringUtils.stripAccents(region);
    }

    public String getOs() {
        if (os == null) {
            return OSType.SUSE.toString();
        }
        return os;
    }
}
