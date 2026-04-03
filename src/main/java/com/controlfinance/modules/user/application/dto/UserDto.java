package com.controlfinance.modules.user.application.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserDto {
  private String id;
  private String name;
  private String email;
  private String cpf;
  private String role;
  private boolean twoFactorEnabled;
}
