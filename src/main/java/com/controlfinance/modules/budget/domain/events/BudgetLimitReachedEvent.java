package com.controlfinance.modules.budget.domain.events;

import com.controlfinance.common.events.DomainEvent;

import java.time.Instant;

public record BudgetLimitReachedEvent(
    String aggregateId,
    String userId,
    Instant occurredAt,
    String yearMonth,
    String categoryId,
    String severity, // WARNING / EXCEEDED
    String message
) implements DomainEvent {
  @Override public String name() { return "BudgetLimitReachedEvent"; }
}
