package com.ag.transactiongatewayexchange.service;

import com.ag.transactiongatewayexchange.dto.PurchaseRequest;
import com.ag.transactiongatewayexchange.model.Purchase;
import com.ag.transactiongatewayexchange.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Transactional
    public boolean savePurchase(PurchaseRequest request) {
        Purchase purchase = new Purchase();
        purchase.setDescription(request.getDescription());
        purchase.setTransactionDate(request.getTransactionDate());
        purchase.setAmount(request.getAmount());

        purchaseRepository.save(purchase);

        return true;
    }
}
