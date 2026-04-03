package com.controlfinance.modules.auth.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
  @NotBlank
  private String name;

  @Email @NotBlank
  private String email;

  @NotBlank
  private String cpf;

  @NotBlank @Size(min = 8)
  private String password;
}
