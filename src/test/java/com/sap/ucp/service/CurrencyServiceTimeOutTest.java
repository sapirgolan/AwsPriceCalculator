package com.sap.ucp.service;

import com.sun.net.httpserver.HttpServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMockMvc

@TestPropertySource(
        properties = {
                "currency.exchange.url=http://localhost:7070/timeout",
        }
)

public class CurrencyServiceTimeOutTest {

    @Autowired
    private ICurrencyService ICurrencyService;

    @Test
    public void timeoutWithCatch() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(7070), 0);

        server.createContext("/timeout", httpExchange -> {
            try {
                TimeUnit.SECONDS.sleep(3L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            httpExchange.sendResponseHeaders(200, 0);
        });
        server.start();

        assertThat(ICurrencyService.getEuroCurrencyFromDollar().get(), closeTo(-1.0, 0.0001));
        server.stop(1);
        assertThat(ICurrencyService.getEuroCurrencyFromDollar().get(), closeTo(-1.0, 0.0001));
    }

}