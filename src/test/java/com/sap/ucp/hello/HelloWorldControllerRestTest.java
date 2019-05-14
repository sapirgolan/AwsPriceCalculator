package com.sap.ucp.hello;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by i062070 on 27/07/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloWorldControllerRestTest {

    public static final String HTTP_LOCALHOST = "http://localhost:";
    public static final String CONTROLLER_NAME = "/hellow-world";
    @LocalServerPort
    private int port;

    /*All REST requests are being invoked using this member*/
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void greetingShouldReturnDefualtMessage() throws Exception {
        String response = restTemplate.getForObject(HTTP_LOCALHOST + port + CONTROLLER_NAME, String.class);
        assertThat(response, containsString("Hello, Jhon Doe!"));
    }

    @Test
    public void greetingWithX_ShouldReturnX() throws Exception {
        String response = restTemplate.getForObject(HTTP_LOCALHOST + port + CONTROLLER_NAME + "?name=amir", String.class);
        assertThat(response, containsString("amir"));
    }

    @Test
//    @Ignore
    public void greetingWithX_ShouldReturnX2() throws Exception {
        ResponseEntity<Greeting> forEntity = restTemplate.getForEntity(HTTP_LOCALHOST + port + CONTROLLER_NAME + "?name=amir", Greeting.class);
        Greeting forObject = restTemplate.getForObject(HTTP_LOCALHOST + port + CONTROLLER_NAME + "?name=amir", Greeting.class);
        String response = restTemplate.getForObject(HTTP_LOCALHOST + port + CONTROLLER_NAME + "?name=amir", String.class);
        assertThat(response, containsString("amir"));
    }

}