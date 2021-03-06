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
        String content = "{\"tShirtSize\":\"x.large\", \"region\":\"Frankfurt\"}";
        ObjectContent<OrderUcp> parse = json.parse(content);
        OrderUcp rate = parse.getObject();
        assertThat(rate.gettShirtSize(), is("x.large"));
        assertThat(rate.getRegion(), is("Frankfurt"));
    }

    @Test
    public void os_tshirt_region() throws Exception {
        String content = "{\"region\":\"Frankfurt\",\"tShirtSize\":\"t2.micro\",\"os\":\"Red Hat Enterprise Linux 7.3 (HVM), SSD Volume Type\"}";
        ObjectContent<OrderUcp> parsed = json.parse(content);
        OrderUcp orderUcp = parsed.getObject();
        assertThat(orderUcp.gettShirtSize(), is("t2.micro"));
        assertThat(orderUcp.getRegion(), is("Frankfurt"));
        assertThat(orderUcp.getOs(), is("Red Hat Enterprise Linux 7.3 (HVM), SSD Volume Type"));
    }
}