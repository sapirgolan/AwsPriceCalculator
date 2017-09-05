package com.sap.ucp.service;

import com.sap.ucp.model.CurrencyRate;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {CurrencyService.class})
@AutoConfigureJson
@AutoConfigureMockMvc
public class CurrencyServiceModuleTest {

    @MockBean
    RestTemplate restTemplate;
    @Autowired
    private CurrencyService currencyService;

    @Test
    public void getEuroCurrencyFromDollar() throws Exception {
        when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
                .thenReturn(new CurrencyRate("usd", "2017-06-01", 0.764));

        double currency = currencyService.getEuroCurrencyFromDollar();
        assertThat(currency, Matchers.closeTo(0.764, 0.0001));
    }

}