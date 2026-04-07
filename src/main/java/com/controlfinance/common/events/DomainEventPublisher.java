package com.controlfinance.common.events;

public interface DomainEventPublisher {
  void publish(DomainEvent event);
}