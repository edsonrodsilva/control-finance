package com.controlfinance.modules.auth.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LoginRequest {
  @Email @NotBlank
  private String email;

  @NotBlank
  private String password;

  /** código 2FA (se habilitado) */
  private String twoFactorCode;
}
