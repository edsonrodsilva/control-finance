package com.controlfinance.modules.transactions.application.usecases;

import com.controlfinance.infrastructure.events.DomainEventPublisher;
import com.controlfinance.infrastructure.security.SecurityUtils;
import com.controlfinance.modules.budget.application.services.BudgetEvaluationService;
import com.controlfinance.modules.transactions.application.dto.TransactionDto;
import com.controlfinance.modules.transactions.application.mapper.TransactionMapper;
import com.controlfinance.modules.transactions.domain.entities.Transaction;
import com.controlfinance.modules.transactions.domain.enums.TransactionStatus;
import com.controlfinance.modules.transactions.domain.enums.TransactionType;
import com.controlfinance.modules.transactions.domain.events.TransactionCreatedEvent;
import com.controlfinance.modules.transactions.domain.repositories.TransactionRepositoryPort;
import com.controlfinance.common.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CreateTransactionUseCase {

  private final TransactionRepositoryPort repo;
  private final TransactionMapper mapper;
  private final DomainEventPublisher events;
  private final BudgetEvaluationService budgetEval;
  private final ComputeMonthSpentService monthSpent;

  public TransactionDto execute(TransactionDto dto) {
    if (dto.getStatus() == null) dto.setStatus(TransactionStatus.PENDING);
    if (dto.getTransactionDate() == null) dto.setTransactionDate(Instant.now());

    String userId = SecurityUtils.currentUserId();
    Transaction tx = mapper.toEntity(dto);
    tx.setId(null);
    tx.setUserId(userId);

    if (tx.getAmount() == null || tx.getAmount().signum() <= 0) {
      throw new BadRequestException("Amount must be positive");
    }

    tx = repo.save(tx);

    events.publish(new TransactionCreatedEvent(tx.getId(), tx.getUserId(), Instant.now(), tx.getId(), tx.getType().name(), tx.getCategoryId()));

    // orçamento: só avalia para despesas
    if (tx.getType() == TransactionType.EXPENSE) {
      BigDecimal spentAfter = monthSpent.spentAfter(tx);
      budgetEval.evaluateOnExpenseCreated(tx, spentAfter);
    }

    return mapper.toDto(tx);
  }
}
