package com.controlfinance.modules.auth.domain.events;

import com.controlfinance.common.events.DomainEvent;

import java.time.Instant;

public record UserRegisteredEvent(
    String aggregateId,
    String userId,
    Instant occurredAt,
    String email
) implements DomainEvent {
  @Override public String name() { return "UserRegisteredEvent"; }
}
