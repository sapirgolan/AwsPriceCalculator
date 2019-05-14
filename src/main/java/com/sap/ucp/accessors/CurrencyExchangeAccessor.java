package com.sap.ucp.accessors;

import com.sap.ucp.config.PropertiesResolver;
import com.sap.ucp.model.CurrencyRate;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class CurrencyExchangeAccessor {

  static final String FIXER_ACCESS_KEY = "fixerAccessKey";
  private final String accessKey;
  @Value("${currency.exchange.url}")
  private String baseUrl;
  private String euroCurrencyPath;
  private UriComponentsBuilder uriComponentsBuilder;

  private final RestTemplate restTemplate;

  public CurrencyExchangeAccessor(RestTemplate restTemplate, PropertiesResolver propertiesResolver) {
    this.restTemplate = restTemplate;
    this.accessKey = propertiesResolver.getProperty(FIXER_ACCESS_KEY);
  }

  @PostConstruct
  private void init() {
    uriComponentsBuilder = UriComponentsBuilder.newInstance()
        .scheme("http")
        .host(baseUrl)
        .path("/latest")
        .queryParam("access_key", accessKey);

    euroCurrencyPath = buildGetEuroCurrencyPath();
  }

  public CurrencyRate getEuroCurrency() {
    return restTemplate.getForObject(euroCurrencyPath, CurrencyRate.class);
  }

  private String buildGetEuroCurrencyPath() {
    return this.uriComponentsBuilder.cloneBuilder()
        .queryParam("symbols", "USD")
        .build()
        .toString();
  }
}
