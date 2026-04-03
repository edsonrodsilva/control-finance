package com.controlfinance.modules.transactions.infrastructure.persistence;

import com.controlfinance.modules.transactions.domain.entities.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TransactionMongoRepository extends MongoRepository<Transaction, String> {
  Optional<Transaction> findByIdAndUserId(String id, String userId);
}
