package com.controlfinance.infrastructure.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.security.encryption")
public class EncryptionProperties {
  /** Base64 do key material (32 bytes) para AES-256-GCM */
  private String keyBase64;
}
