package com.sap.ucp.service;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import com.sap.ucp.config.PropertiesResolver;
import com.sap.ucp.model.CurrencyRate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyServiceModuleTest {

    @Mock
    RestTemplate restTemplate;
    @Mock
    PropertiesResolver propertiesResolver;

    private ICurrencyService currencyService;

    @Before
    public void setUp() {
        when(propertiesResolver.getProperty(anyString())).thenReturn("mockedAccessKey");
        currencyService = new CurrencyService(restTemplate, propertiesResolver);
    }

    @Test
    public void getEuroCurrencyFromDollar() {
        when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
                .thenReturn(new CurrencyRate("usd", "2017-06-01", 0.764));

        Double currency = currencyService.getEuroCurrencyFromDollar();
        assertThat(currency, closeTo(0.764, 0.0001));
    }

}