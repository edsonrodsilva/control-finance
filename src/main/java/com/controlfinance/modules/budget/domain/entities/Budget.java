package com.controlfinance.modules.budget.domain.entities;

import com.controlfinance.shared.base.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Document(collection = "budgets")
public class Budget extends BaseDocument {
  private String yearMonth; // yyyy-MM
  private String categoryId;
  private BigDecimal limitAmount;
}
