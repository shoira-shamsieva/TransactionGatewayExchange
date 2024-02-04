package com.ag.transactiongatewayexchange.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseRequest {
    private String description;
    private LocalDate transactionDate;
    private BigDecimal amount;
}
