package com.controlfinance.modules.audit.infrastructure.persistence;

import com.controlfinance.modules.audit.domain.entities.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogMongoRepository extends MongoRepository<AuditLog, String> {
}
