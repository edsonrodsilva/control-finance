package com.controlfinance.modules.user.domain.entities;

import com.controlfinance.shared.base.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Document(collection = "users")
public class User extends BaseDocument {

  private String name;

  @Indexed(unique = true)
  private String email;

  @Indexed(unique = true)
  private String cpf;

  private String passwordHash;

  private String role; // USER / ADMIN

  private boolean twoFactorEnabled;

  /** segredo TOTP criptografado */
  private String twoFactorSecretEnc;

  private Instant lastLoginAt;
}
