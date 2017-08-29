package com.sap.ucp.service;

import com.sap.ucp.model.CurrencyRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyService {
    @Autowired
    RestTemplate restTemplate;

    public double getEuroCurrencyFromDollar() {
        CurrencyRate rate = restTemplate.getForObject("http://api.fixer.io/latest?symbols=USD,EUR&base=USD", CurrencyRate.class);
        return rate.getRate();
    }
}
