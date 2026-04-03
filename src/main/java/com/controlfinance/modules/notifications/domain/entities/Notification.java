package com.controlfinance.modules.notifications.domain.entities;

import com.controlfinance.shared.base.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Document(collection = "notifications")
public class Notification extends BaseDocument {
  private String title;
  private String message;
  private String severity; // INFO/WARNING/CRITICAL
  private boolean read;
}
