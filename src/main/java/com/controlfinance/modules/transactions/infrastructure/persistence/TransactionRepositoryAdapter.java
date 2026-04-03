package com.controlfinance.modules.transactions.infrastructure.persistence;

import com.controlfinance.modules.transactions.domain.entities.Transaction;
import com.controlfinance.modules.transactions.domain.repositories.TransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

  private final TransactionMongoRepository repo;
  private final MongoTemplate template;

  @Override
  public Transaction save(Transaction t) { return repo.save(t); }

  @Override
  public Optional<Transaction> findByIdAndUserId(String id, String userId) { return repo.findByIdAndUserId(id, userId); }

  @Override
  public void deleteById(String id) { repo.deleteById(id); }

  @Override
  public List<Transaction> search(String userId, Instant from, Instant to, String categoryId, String type, String status) {
    Criteria c = Criteria.where("userId").is(userId);

    if (from != null || to != null) {
      Criteria dc = Criteria.where("transactionDate");
      if (from != null) dc = dc.gte(from);
      if (to != null) dc = dc.lte(to);
      c = new Criteria().andOperator(c, dc);
    }

    if (categoryId != null && !categoryId.isBlank()) {
      c = new Criteria().andOperator(c, Criteria.where("categoryId").is(categoryId));
    }

    if (type != null && !type.isBlank()) {
      c = new Criteria().andOperator(c, Criteria.where("type").is(type));
    }

    if (status != null && !status.isBlank()) {
      c = new Criteria().andOperator(c, Criteria.where("status").is(status));
    }

    Query q = new Query(c);
    q.limit(200);
    q.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "transactionDate"));
    return template.find(q, Transaction.class);
  }
}
