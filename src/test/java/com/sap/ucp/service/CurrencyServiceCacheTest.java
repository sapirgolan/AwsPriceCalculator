package com.sap.ucp.service;

import com.sap.ucp.model.CurrencyRate;
import com.sap.ucp.rules.CacheInvalidation;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)

public class CurrencyServiceCacheTest {

    @Rule
    @Autowired
    public CacheInvalidation cacheInvalidationRule;

    @MockBean
    RestTemplate restTemplate;

    @Autowired
    private ICurrencyService currencyService;

    @Autowired
    private CacheManager cacheManager;


    private Cache cache;

    @Before
    public void setUp() throws Exception {
        cache = assertCacheIsEmpty();
    }

    @Test
    public void getX_cacheX() throws Exception {
        when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
                .thenReturn(new CurrencyRate("test", "TDD_DATA", 0.777))
                .thenReturn(new CurrencyRate("test", "TDD_DATA", 0.888));

        Double exchRateFirst = currencyService.getEuroCurrencyFromDollar();

        Double exchRateSecond = currencyService.getEuroCurrencyFromDollar();
        Double exchRateFirstValue = exchRateFirst;
        Double exchRateSecondValue = exchRateSecond;
        assertThat(exchRateFirstValue, closeTo(exchRateSecondValue, 0.0001));
        assertThat(exchRateFirstValue, closeTo(0.777, 0.0001));

        Double currency = cache.get(SimpleKey.EMPTY, Double.class);
        assertThat(currency, closeTo(0.777, 0.0001));

        assertThat(currencyService.getEuroCurrencyFromDollar(), closeTo(exchRateFirstValue, 0.001));
    }

    private Cache assertCacheIsEmpty() {
        Cache cache = cacheManager.getCache("eurCurrency");
        Assert.assertThat("cache is not empty", cache.get(SimpleKey.EMPTY), Matchers.nullValue());
        return cache;
    }

    @Test
    public void expThrown_cacheNone() throws Exception {
        when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
                .thenThrow(RestClientException.class)
                .thenReturn(new CurrencyRate("test", "TDD_DATA", 0.888));

        Double exchRateFirst = currencyService.getEuroCurrencyFromDollar();
        Double exchRateSecond = currencyService.getEuroCurrencyFromDollar();


        assertThat(0.888, isOneOf(exchRateFirst, exchRateSecond));
        Double valueInCache = cache.get(SimpleKey.EMPTY, Double.class);
        assertThat(valueInCache, closeTo(0.888, 0.0001));
    }
}