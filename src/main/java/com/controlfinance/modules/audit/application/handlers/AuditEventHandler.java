package com.controlfinance.modules.audit.application.handlers;

import com.controlfinance.infrastructure.events.DomainEvent;
import com.controlfinance.modules.audit.domain.entities.AuditLog;
import com.controlfinance.modules.audit.infrastructure.persistence.AuditLogMongoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventHandler {

  private final AuditLogMongoRepository repo;
  private final ObjectMapper mapper;

  @EventListener
  public void on(DomainEvent event) {
    try {
      String payload = mapper.writeValueAsString(event);
      AuditLog logEntry = AuditLog.builder()
          .eventName(event.name())
          .aggregateId(event.aggregateId())
          .payload(payload)
          .build();
      logEntry.setUserId(event.userId());
      repo.save(logEntry);
    } catch (Exception e) {
      log.warn("audit_failed event={} msg={}", event.name(), e.getMessage());
    }
  }
}
