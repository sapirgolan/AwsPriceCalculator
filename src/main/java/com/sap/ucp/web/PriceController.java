package com.sap.ucp.web;

import com.sap.ucp.model.OrderUcp;
import com.sap.ucp.model.PriceEstimation;
import com.sap.ucp.service.ICurrencyService;
import com.sap.ucp.service.PriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(PriceController.REST_NAME)
public class PriceController {

    public static final String REST_NAME = "/v1.0/aws";
    public static final double ERROR_PRICE = -1.0;
    public static final int MINIMUM_PRICE = 0;
    private static Logger logger = LoggerFactory.getLogger(PriceController.class);


    @Autowired
    PriceService priceService;

    @Autowired
    ICurrencyService ICurrencyService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<PriceEstimation> computePrice(
            @RequestBody OrderUcp order) throws ExecutionException, InterruptedException {

        double price = priceService.calculateHourlyPrice(order.gettShirtSize(), order.getRegion(), getHoursInMonth());
        if (price < MINIMUM_PRICE) {
            logger.warn("calculated price was below " + MINIMUM_PRICE);
            return new ResponseEntity<>(new PriceEstimation(ERROR_PRICE), HttpStatus.NOT_FOUND);
        }

        Double euroRate = ICurrencyService.getEuroCurrencyFromDollar();
        if (euroRate <= MINIMUM_PRICE) {
            logger.error("Failed to convert USD to other currency");
            return new ResponseEntity<>(new PriceEstimation(ERROR_PRICE), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new PriceEstimation(price * euroRate), HttpStatus.OK);
    }

    private int getHoursInMonth() {
        int avengeDaysInMonth = 30;
        return (int) TimeUnit.DAYS.toHours(avengeDaysInMonth);
    }

    @ExceptionHandler
    public ResponseEntity<PriceEstimation> err(HttpServletRequest request, Exception ex) {
        logger.error("failed on " + request.getRequestURL(), ex.getMessage());
        return new ResponseEntity<>(new PriceEstimation(ERROR_PRICE), HttpStatus.NOT_FOUND);
    }
}
