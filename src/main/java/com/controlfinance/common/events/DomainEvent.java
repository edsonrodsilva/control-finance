package com.controlfinance.common.events;

import java.time.Instant;

public interface DomainEvent {
  String name();
  Instant occurredAt();
  String aggregateId();
  String userId();
}