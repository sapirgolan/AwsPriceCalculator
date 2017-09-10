package com.sap.ucp.service;

import com.sap.ucp.rules.Retry;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureJson
@AutoConfigureMockMvc
public class CurrencyServiceIntegrationTest {

    @Rule
    public Timeout globalTimeOut = Timeout.seconds(20);

    @Rule
    public Retry retry = new Retry(3);

    @Autowired
    private ICurrencyService currencyService;

    @Test
    public void getEuroCurrencyFromDollarIntegratedWithInternet() throws Exception {
        CompletableFuture<Double> currency = currencyService.getEuroCurrencyFromDollar();
        assertThat(currency.get(), Matchers.greaterThan(0.000));
    }
}