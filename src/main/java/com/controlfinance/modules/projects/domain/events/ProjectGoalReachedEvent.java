package com.controlfinance.modules.projects.domain.events;

import com.controlfinance.common.events.DomainEvent;

import java.time.Instant;

public record ProjectGoalReachedEvent(
    String aggregateId,
    String userId,
    Instant occurredAt,
    String projectId,
    String projectName
) implements DomainEvent {
    @Override public String name() { return "ProjectGoalReachedEvent"; }
}
