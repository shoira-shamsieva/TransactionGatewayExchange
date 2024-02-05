package com.ag.transactiongatewayexchange.controller;

import com.ag.transactiongatewayexchange.dto.PurchaseDTO;
import com.ag.transactiongatewayexchange.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/api/purchase")
    public @ResponseBody UUID putPurchase(@RequestBody PurchaseDTO request) {
        System.out.println("Received request " + request);
        return purchaseService.savePurchase(request);
    }
}
