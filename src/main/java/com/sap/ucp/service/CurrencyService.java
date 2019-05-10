package com.sap.ucp.service;

import com.sap.ucp.config.PropertiesResolver;
import com.sap.ucp.model.CurrencyRate;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CurrencyService implements ICurrencyService {

    private static final String FIXER_ACCESS_KEY = "fixerAccessKey";
    private final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    @Value("${currency.exchange.url}")
    private String baseUrl;
    private UriComponentsBuilder uriComponentsBuilder;
    private String getEuroCurrencyPath;

    private final RestTemplate restTemplate;
    private final PropertiesResolver propertiesResolver;

    public CurrencyService(RestTemplate restTemplate, PropertiesResolver propertiesResolver) {
        this.restTemplate = restTemplate;
        this.propertiesResolver = propertiesResolver;
    }

    @PostConstruct
    public void postConstruct() {
        String accessKey = propertiesResolver.getProperty(FIXER_ACCESS_KEY);
        uriComponentsBuilder = UriComponentsBuilder.newInstance()
            .scheme("http")
            .host(baseUrl)
            .path("/latest")
            .queryParam("access_key", accessKey);

        getEuroCurrencyPath = buildGetEuroCurrencyPath();
    }
    @Override
    @Cacheable(value = "eurCurrency", sync = true)
    public Double getEuroCurrencyFromDollar() {
        Double rateVal;
        try {
            CurrencyRate rate = restTemplate.getForObject(getEuroCurrencyPath, CurrencyRate.class);
            rateVal = rate.getRates();
        } catch (RestClientException e) {
            rateVal = null;
            logger.error("Failed to obtain currency exchange rate", e);
        }
        return rateVal;
    }

    private String buildGetEuroCurrencyPath() {
        return this.uriComponentsBuilder.cloneBuilder()
            .queryParam("symbols", "USD")
            .build()
            .toString();
    }
}
