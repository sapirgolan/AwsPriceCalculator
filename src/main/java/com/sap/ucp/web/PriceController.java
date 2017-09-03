package com.sap.ucp.web;

import com.sap.ucp.model.OrderUcp;
import com.sap.ucp.model.PriceEstimation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1.0/aws")
public class PriceController {

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    PriceEstimation computePrice(
            @RequestBody OrderUcp order) {
        return new PriceEstimation(414.18);
    }
}
