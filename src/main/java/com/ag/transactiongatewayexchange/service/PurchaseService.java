package com.ag.transactiongatewayexchange.service;

import com.ag.transactiongatewayexchange.dto.PurchaseDTO;
import com.ag.transactiongatewayexchange.model.Purchase;
import com.ag.transactiongatewayexchange.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;


@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private CurrencyConverterService currencyConverterService;


    @Transactional
    public UUID savePurchase(PurchaseDTO request) {
        Purchase purchase = new Purchase();
        purchase.setDescription(request.getDescription());
        purchase.setTransactionDate(request.getTransactionDate());
        purchase.setAmount(request.getAmount());

        purchaseRepository.save(purchase);
        System.out.println(purchase.getUniqueIdentifier());
        return purchase.getUniqueIdentifier();
    }

    public PurchaseDTO retrievePurchaseById(UUID uuid, String targetCurrency) {
        Purchase purchase = purchaseRepository.findByUniqueIdentifier(uuid);

        if (purchase != null) {
            // If a target currency is specified, perform the conversion
            if (targetCurrency != null && !targetCurrency.isEmpty()) {
                BigDecimal convertedAmount = currencyConverterService.convertToCurrency(purchase.getAmount(),
                        targetCurrency,
                        purchase.getTransactionDate());
                purchase.setAmount(convertedAmount);
                purchase.setCurrency(targetCurrency);
            }

            return new PurchaseDTO(purchase.getDescription(), purchase.getTransactionDate(), purchase.getAmount(), purchase.getCurrency());
        } else {
            //TODO need clarification how to handle the case where the purchase with the given UUID is not found
            return null;
        }
    }
}
