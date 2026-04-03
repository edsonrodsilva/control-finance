package com.controlfinance.transactions;

import com.controlfinance.modules.transactions.domain.entities.Transaction;
import com.controlfinance.modules.transactions.domain.enums.TransactionStatus;
import com.controlfinance.modules.transactions.domain.enums.TransactionType;
import com.controlfinance.modules.transactions.infrastructure.persistence.TransactionMongoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;

@Testcontainers
@SpringBootTest
class TransactionRepositoryIT {

  @Container
  static MongoDBContainer mongo = new MongoDBContainer("mongo:7");

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
  }

  @Autowired
  TransactionMongoRepository repo;

  @Test
  void shouldSaveAndLoad() {
    Transaction t = Transaction.builder()
        .type(TransactionType.EXPENSE)
        .categoryId("cat")
        .amount(new BigDecimal("10.00"))
        .status(TransactionStatus.PAID)
        .transactionDate(Instant.now())
        .description("coffee")
        .build();
    t.setUserId("u1");

    Transaction saved = repo.save(t);
    Assertions.assertNotNull(saved.getId());

    var loaded = repo.findById(saved.getId()).orElseThrow();
    Assertions.assertEquals("u1", loaded.getUserId());
  }
}
