package com.controlfinance.modules.categories.domain.entities;

import com.controlfinance.shared.base.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Document(collection = "subcategories")
public class SubCategory extends BaseDocument {
  private String categoryId;
  private String name;
}
