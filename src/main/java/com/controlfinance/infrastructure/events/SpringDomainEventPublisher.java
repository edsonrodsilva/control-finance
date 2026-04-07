package com.controlfinance.infrastructure.events;

import com.controlfinance.common.events.DomainEvent;
import com.controlfinance.common.events.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventPublisher {
  private final ApplicationEventPublisher publisher;

  @Override
  public void publish(DomainEvent event) {
    publisher.publishEvent(event);
  }
}
