package com.ag.transactiongatewayexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PurchaseWithExchangeRateDTO {
    private String description;
    private LocalDate transactionDate;
    private BigDecimal amount;
    private BigDecimal exchangeRateUsed;
    private BigDecimal convertedAmount;
}
