package com.controlfinance.modules.budget.application.services;

import com.controlfinance.infrastructure.events.DomainEventPublisher;
import com.controlfinance.modules.budget.domain.events.BudgetLimitReachedEvent;
import com.controlfinance.modules.budget.domain.repositories.BudgetRepositoryPort;
import com.controlfinance.modules.transactions.domain.entities.Transaction;
import com.controlfinance.shared.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BudgetEvaluationService {

  private final BudgetRepositoryPort budgets;
  private final DomainEventPublisher events;

  /**
   * Avalia orçamento na criação de uma despesa.
   * Regra: alerta ao atingir 90% e evento de excedido ao ultrapassar 100%.
   *
   * OBS: Para um backend fintech-like, o cálculo real deve considerar somatório mensal por categoria.
   * Aqui deixamos o hook + evento, e o somatório é feito no handler de transações.
   */
  public void evaluateOnExpenseCreated(Transaction tx, BigDecimal monthSpentAfter) {
    if (tx.getCategoryId() == null) return;

    String ym = DateUtils.ym(tx.getTransactionDate(), ZoneId.systemDefault()).toString();
    budgets.findByUserIdAndYearMonthAndCategoryId(tx.getUserId(), ym, tx.getCategoryId()).ifPresent(b -> {
      BigDecimal limit = b.getLimitAmount();
      if (limit == null || limit.signum() <= 0) return;

      BigDecimal ratio = monthSpentAfter.divide(limit, 4, java.math.RoundingMode.HALF_UP);

      if (ratio.compareTo(new BigDecimal("1.0")) >= 0) {
        events.publish(new BudgetLimitReachedEvent(b.getId(), tx.getUserId(), Instant.now(), ym, tx.getCategoryId(),
            "EXCEEDED", "Orçamento excedido para a categoria"));
      } else if (ratio.compareTo(new BigDecimal("0.9")) >= 0) {
        events.publish(new BudgetLimitReachedEvent(b.getId(), tx.getUserId(), Instant.now(), ym, tx.getCategoryId(),
            "WARNING", "Orçamento atingiu 90% para a categoria"));
      }
    });
  }
}
