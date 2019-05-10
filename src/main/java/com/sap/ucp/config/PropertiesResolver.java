package com.sap.ucp.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class PropertiesResolver {

  public String getProperty(String propertyName) {
    return System.getProperty(propertyName, StringUtils.EMPTY);
  }

}
