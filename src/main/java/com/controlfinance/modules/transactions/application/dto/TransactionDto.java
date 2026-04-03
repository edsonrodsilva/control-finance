package com.controlfinance.modules.transactions.application.dto;

import com.controlfinance.modules.transactions.domain.enums.TransactionStatus;
import com.controlfinance.modules.transactions.domain.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TransactionDto {
  private String id;

  @NotNull
  private TransactionType type;

  @NotBlank
  private String categoryId;

  private String subCategoryId;

  @NotNull @Positive
  private BigDecimal amount;

  private String description;

  private TransactionStatus status;

  @NotNull
  private Instant transactionDate;

  private String paymentTicket;
}
