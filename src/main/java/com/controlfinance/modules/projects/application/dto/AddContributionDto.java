package com.controlfinance.modules.projects.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AddContributionDto {
  @NotNull @Positive
  private BigDecimal amount;
}
