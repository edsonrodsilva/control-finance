package com.controlfinance.modules.audit.domain.repositories;

import com.controlfinance.modules.audit.domain.entities.AuditLog;

public interface AuditLogRepositoryPort {
  AuditLog save(AuditLog log);
}