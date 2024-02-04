package com.ag.transactiongatewayexchange.controller;

import com.ag.transactiongatewayexchange.dto.PurchaseRequest;
import com.ag.transactiongatewayexchange.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/api/purchase")
    public String putPurchase(@RequestBody PurchaseRequest request) {
        System.out.println("Received request "+ request);
        purchaseService.savePurchase(request);
        return "Successfully Saved";
    }

}
