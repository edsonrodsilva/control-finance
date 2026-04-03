package com.controlfinance.modules.reporting.application.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DashboardSummaryDto {
  private BigDecimal totalIncome;
  private BigDecimal totalExpense;
  private BigDecimal balance;
}
