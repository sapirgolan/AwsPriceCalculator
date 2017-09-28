package com.sap.ucp.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.ucp.model.OrderUcp;
import com.sap.ucp.service.ICurrencyService;
import com.sap.ucp.service.PriceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;

import static org.hamcrest.Matchers.closeTo;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PriceController.class)
public class PriceControllerWebTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    PriceService priceService;
    @MockBean
    ICurrencyService ICurrencyService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenRequestContainsInvalidData_404IsReturned() throws Exception {
        OrderUcp orderUcp = new OrderUcp("t2.medium", "roosevelt");
        when(priceService.calculateHourlyPrice(orderUcp, 24))
                .thenReturn(PriceService.ERROR_PRICE);

        MockHttpServletRequestBuilder content = post(PriceController.REST_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderUcp));
        mockMvc.perform(content)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.price", closeTo(-1.0, 0.000001)));
    }

    @Test
    public void whenExceptionThrown_defaultErrorHandling() throws Exception {
        OrderUcp orderUcp = new OrderUcp("t2.medium", "roosevelt");
        when(priceService.calculateHourlyPrice(eq(orderUcp), anyInt()))
                .thenThrow(IOException.class);

        MockHttpServletRequestBuilder content = post(PriceController.REST_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderUcp));
        mockMvc.perform(content)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.price", closeTo(-1.0, 0.000001)));

    }

    @Test
    public void whenConvectionRateIsX_priceIsMultipliedByX() throws Exception {
        OrderUcp orderUcp = new OrderUcp("t2.medium", "Frankfurt");
        when(priceService.calculateHourlyPrice(any(OrderUcp.class), anyInt()))
                .thenReturn(3.0);
        when(ICurrencyService.getEuroCurrencyFromDollar())
                .thenReturn(0.8);

        MockHttpServletRequestBuilder content = post(PriceController.REST_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderUcp));
        mockMvc.perform(content)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price", closeTo(2.4, 0.000001)));
    }

    @Test
    public void whenCurrncyServiceFail_defaultErrorHandling() throws Exception {
        OrderUcp orderUcp = new OrderUcp("t2.medium", "Frankfurt");

        when(ICurrencyService.getEuroCurrencyFromDollar())
                .thenReturn(null);
        when(priceService.calculateHourlyPrice(eq(orderUcp), anyInt()))
                .thenReturn(3.0);

        MockHttpServletRequestBuilder content = post(PriceController.REST_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderUcp));
        mockMvc.perform(content)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.price", closeTo(-1.0, 0.000001)));
    }
}