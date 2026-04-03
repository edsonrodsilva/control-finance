package com.controlfinance.infrastructure.events;

import java.time.Instant;

public interface DomainEvent {
  String name();
  Instant occurredAt();
  String aggregateId();
  String userId();
}
