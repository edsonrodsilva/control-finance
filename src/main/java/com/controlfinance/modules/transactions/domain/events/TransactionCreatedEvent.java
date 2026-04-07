package com.controlfinance.modules.transactions.domain.events;

import com.controlfinance.common.events.DomainEvent;

import java.time.Instant;

public record TransactionCreatedEvent(
    String aggregateId,
    String userId,
    Instant occurredAt,
    String transactionId,
    String type,
    String categoryId
) implements DomainEvent {
  @Override public String name() { return "TransactionCreatedEvent"; }
}
