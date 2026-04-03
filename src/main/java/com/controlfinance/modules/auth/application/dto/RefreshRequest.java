package com.controlfinance.modules.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RefreshRequest {
  @NotBlank
  private String refreshToken;
}
