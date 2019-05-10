package com.sap.ucp.model;

import com.sap.ucp.context.ApplicationTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApplicationTestConfiguration.class)
@JsonTest
public class CurrencyRatesTest {

    @Autowired
    private JacksonTester<CurrencyRate> json;

    @Test
    public void testDeserialize() throws Exception {
        String content = "{\"base\":\"EUR\",\"date\":\"2017-08-23\",\"rates\":{\"USD\":1.84753}}";
        ObjectContent<CurrencyRate> parse = json.parse(content);
        CurrencyRate rate = parse.getObject();
        assertThat(rate.getBase(), is("EUR"));
        assertThat(rate.getDate(), is("2017-08-23"));
        assertThat(rate.getRates(), closeTo(1.84753, 0.0001));
    }
}