package com.controlfinance.modules.categories.domain.entities;

import com.controlfinance.shared.base.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Document(collection = "categories")
public class Category extends BaseDocument {
  private String name;
  private String type; // income / expense / both
}
