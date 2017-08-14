package com.sap.ucp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Created by i062070 on 14/08/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "attributes")
public class ProductAttributes {
    private String instanceType;
    private String preInstalledSw;
    private String location;

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getPreInstalledSw() {
        return preInstalledSw;
    }

    public void setPreInstalledSw(String preInstalledSw) {
        this.preInstalledSw = preInstalledSw;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
