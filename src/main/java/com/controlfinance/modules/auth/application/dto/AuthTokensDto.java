package com.controlfinance.modules.auth.application.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthTokensDto {
  private String accessToken;
  private String refreshToken;
}
