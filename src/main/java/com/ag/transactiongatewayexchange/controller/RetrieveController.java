package com.ag.transactiongatewayexchange.controller;

import com.ag.transactiongatewayexchange.dto.PurchaseDTO;
import com.ag.transactiongatewayexchange.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RetrieveController {
    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("/api/purchases/{uuid}")
    public PurchaseDTO retrievePurchase(@PathVariable UUID uuid, @RequestParam(required = false) String currency) {
        System.out.println("UUID received " + uuid + " currency " + currency);
        PurchaseDTO purchase = purchaseService.retrievePurchaseById(uuid, currency);

        if (purchase != null) {
            return purchase;
        } else {
            // Handle the case where the purchase with the given UUID is not found
            System.out.println(uuid + " not found");
            return null;
        }
    }
}
