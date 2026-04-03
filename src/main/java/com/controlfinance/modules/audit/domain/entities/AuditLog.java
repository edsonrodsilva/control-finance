package com.controlfinance.modules.audit.domain.entities;

import com.controlfinance.shared.base.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Document(collection = "audit_logs")
public class AuditLog extends BaseDocument {
  private String eventName;
  private String aggregateId;
  private String payload;
}
