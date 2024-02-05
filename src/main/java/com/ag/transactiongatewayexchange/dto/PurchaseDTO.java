package com.ag.transactiongatewayexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PurchaseDTO {
    private String description;
    private LocalDate transactionDate;
    private BigDecimal amount;
    private String currency;
}
