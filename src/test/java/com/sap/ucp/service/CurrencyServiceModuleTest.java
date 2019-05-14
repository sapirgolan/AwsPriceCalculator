package com.sap.ucp.service;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import com.sap.ucp.accessors.CurrencyExchangeAccessor;
import com.sap.ucp.model.CurrencyRate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class CurrencyServiceModuleTest {

    @Mock
    CurrencyExchangeAccessor currencyExchangeAccessor;

    private ICurrencyService currencyService;

    @Before
    public void setUp() {
        currencyService = new CurrencyService(currencyExchangeAccessor);
    }

    @Test
    public void getEuroCurrencyFromDollar() {
        when(currencyExchangeAccessor.getEuroCurrency())
                .thenReturn(new CurrencyRate("usd", "2017-06-01", 0.764));

        Double currency = currencyService.getEuroCurrencyFromDollar();
        assertThat(currency, closeTo(0.764, 0.0001));
    }

}