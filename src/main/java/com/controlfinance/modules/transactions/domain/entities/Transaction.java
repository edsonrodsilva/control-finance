package com.controlfinance.modules.transactions.domain.entities;

import com.controlfinance.modules.transactions.domain.enums.TransactionStatus;
import com.controlfinance.modules.transactions.domain.enums.TransactionType;
import com.controlfinance.common.base.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Document(collection = "transactions")
public class Transaction extends BaseDocument {

  private TransactionType type;

  private String categoryId;
  private String subCategoryId;

  private BigDecimal amount;
  private String description;

  private TransactionStatus status;

  /** data do lançamento */
  private Instant transactionDate;

  /** comprovante / ticket do pagamento */
  private String paymentTicket;
}
