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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApplicationTestConfiguration.class)
@JsonTest
public class OrderUcpTest {

    @Autowired
    private JacksonTester<OrderUcp> json;

    @Test
    public void testDeserialize() throws Exception {
        String content = "{\"tShirtSize\":\"x.large\"}";
        ObjectContent<OrderUcp> parse = json.parse(content);
        OrderUcp rate = parse.getObject();
        assertThat(rate.gettShirtSize(), is("x.large"));
    }

}