package com.sap.ucp.service;

import com.sap.ucp.model.CurrencyRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyService implements ICurrencyService {

    public static final String QUERY_PARAMS = "?symbols=USD,EUR&base=USD";
    private final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
    @Value("${currency.exchange.url}")
    private String baseUrl;

    @Autowired
    RestTemplate restTemplate;

    @Override
    @Cacheable(value = "eurCurrency", sync = true)
    public Double getEuroCurrencyFromDollar() {
        Double rateVal;
        try {
            CurrencyRate rate = restTemplate.getForObject(baseUrl + QUERY_PARAMS, CurrencyRate.class);
            rateVal = rate.getRate();
        } catch (RestClientException e) {
            rateVal = null;
            logger.error("Failed to obtain currency exchange rate", e);
        }
        return rateVal;
    }
}
