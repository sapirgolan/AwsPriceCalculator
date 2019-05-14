package com.sap.ucp.service;

import com.sap.ucp.accessors.CurrencyExchangeAccessor;
import com.sap.ucp.model.CurrencyRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class CurrencyService implements ICurrencyService {

    private final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    private final CurrencyExchangeAccessor currencyExchangeAccessor;

    public CurrencyService(CurrencyExchangeAccessor currencyExchangeAccessor) {
        this.currencyExchangeAccessor = currencyExchangeAccessor;
    }

    @Override
    @Cacheable(value = "eurCurrency", unless = "#result == null")
    public Double getEuroCurrencyFromDollar() {
        Double rateVal;
        try {
            CurrencyRate rate = currencyExchangeAccessor.getEuroCurrency();
            rateVal = rate.getRates();
        } catch (RestClientException e) {
            rateVal = null;
            logger.error("Failed to obtain currency exchange rate", e);
        }
        return rateVal;
    }
}
