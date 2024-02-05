package com.ag.transactiongatewayexchange.service;

import com.ag.transactiongatewayexchange.dto.PurchaseDTO;
import com.ag.transactiongatewayexchange.dto.PurchaseWithExchangeRateDTO;
import com.ag.transactiongatewayexchange.model.Purchase;
import com.ag.transactiongatewayexchange.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    public PurchaseWithExchangeRateDTO retrievePurchaseById(UUID uuid, String targetCurrency) {
        Purchase purchase = purchaseRepository.findByUniqueIdentifier(uuid);
        PurchaseWithExchangeRateDTO purchaseWithExchangeRateDTO = new PurchaseWithExchangeRateDTO(
                purchase.getDescription(),
                purchase.getTransactionDate(),
                purchase.getAmount(),
                BigDecimal.ONE,
                purchase.getAmount());
            // If a target currency is specified, perform the conversion
            if (targetCurrency != null && !targetCurrency.isEmpty()) {
                return currencyConverterService.convertToCurrency(purchaseWithExchangeRateDTO,
                        standardizeCurrency(targetCurrency));
            }

            return purchaseWithExchangeRateDTO;
    }

    private String standardizeCurrency(String currency) {
        if (currency != null && !currency.isEmpty()) {
            return StringUtils.capitalize(currency.toLowerCase());
        } else {
            return null;
        }
    }
}
