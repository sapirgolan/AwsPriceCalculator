package com.sap.ucp.service;

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
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {CurrencyService.class, RestTemplate.class})
@AutoConfigureJson
@AutoConfigureMockMvc
public class CurrencyServiceIntegrationTest {

    @Rule
    public Timeout globalTimeOut = Timeout.seconds(5);

    @Autowired
    private CurrencyService currencyService;

    @Test
    public void getEuroCurrencyFromDollarIntegratedWithInternet() throws Exception {
        double currency = currencyService.getEuroCurrencyFromDollar();
        assertThat(currency, Matchers.greaterThan(0.000));
    }

}