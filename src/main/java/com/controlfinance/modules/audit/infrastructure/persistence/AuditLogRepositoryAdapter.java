package com.controlfinance.modules.audit.infrastructure.persistence;

import com.controlfinance.modules.audit.domain.entities.AuditLog;
import com.controlfinance.modules.audit.domain.repositories.AuditLogRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryAdapter implements AuditLogRepositoryPort {
  private final AuditLogMongoRepository repo;

  @Override
  public AuditLog save(AuditLog log) {
    return repo.save(log);
  }
}