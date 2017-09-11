package com.sap.ucp.service;

import org.springframework.cache.annotation.Cacheable;

public interface ICurrencyService {
    @Cacheable(value = "eurCurrency", sync = true)
    Double getEuroCurrencyFromDollar();
}
