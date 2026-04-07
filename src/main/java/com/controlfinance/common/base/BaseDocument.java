package com.controlfinance.common.base;

import lombok.*;
import org.springframework.data.annotation.*;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public abstract class BaseDocument {
  @Id
  private String id;

  /**
   * Ownership multi-tenant (por usuário). Sempre que existir, deve ser usado para filtrar consultas.
   */
  private String userId;

  @CreatedDate
  private Instant createdAt;

  @LastModifiedDate
  private Instant updatedAt;
}
