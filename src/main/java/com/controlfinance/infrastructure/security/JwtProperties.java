package com.controlfinance.infrastructure.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {
  private String secret;
  private String refreshSecret;
  private long accessTtlMinutes;
  private long refreshTtlDays;
}
