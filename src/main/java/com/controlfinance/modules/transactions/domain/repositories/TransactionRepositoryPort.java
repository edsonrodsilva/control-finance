package com.controlfinance.modules.transactions.domain.repositories;

import com.controlfinance.modules.transactions.domain.entities.Transaction;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryPort {
  Transaction save(Transaction t);
  Optional<Transaction> findByIdAndUserId(String id, String userId);
  void deleteById(String id);

  List<Transaction> search(String userId,
                           Instant from,
                           Instant to,
                           String categoryId,
                           String type,
                           String status);
}
