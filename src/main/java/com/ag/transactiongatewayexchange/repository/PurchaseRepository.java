package com.ag.transactiongatewayexchange.repository;

import com.ag.transactiongatewayexchange.model.Purchase;
import org.springframework.data.repository.CrudRepository;

public interface PurchaseRepository extends CrudRepository<Purchase, Integer>{
}
