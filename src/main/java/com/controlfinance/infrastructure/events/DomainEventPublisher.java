package com.controlfinance.infrastructure.events;

public interface DomainEventPublisher {
  void publish(DomainEvent event);
}
