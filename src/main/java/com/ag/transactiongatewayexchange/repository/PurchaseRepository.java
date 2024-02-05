package com.ag.transactiongatewayexchange.repository;

import com.ag.transactiongatewayexchange.model.Purchase;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PurchaseRepository extends CrudRepository<Purchase, Integer>{
    Purchase findByUniqueIdentifier(UUID uniqueIdentifier);
}
