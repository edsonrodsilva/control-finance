package com.controlfinance.modules.projects.domain.events;

import com.controlfinance.common.events.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;


public record ProjectCreatedEvent(
    String aggregateId,
    String userId,
    Instant occurredAt,
    String projectId,
    String projectName,
    BigDecimal goalAmount
) implements DomainEvent {
    @Override public String name() { return "ProjectCreatedEvent"; }
}
