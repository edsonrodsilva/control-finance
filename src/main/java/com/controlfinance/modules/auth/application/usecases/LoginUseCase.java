package com.controlfinance.modules.auth.application.usecases;

import com.controlfinance.infrastructure.security.CryptoService;
import com.controlfinance.infrastructure.security.JwtService;
import com.controlfinance.modules.auth.application.dto.AuthTokensDto;
import com.controlfinance.modules.auth.application.dto.LoginRequest;
import com.controlfinance.modules.user.domain.repositories.UserRepositoryPort;
import com.controlfinance.modules.user.domain.services.TotpService;
import com.controlfinance.common.exceptions.BadRequestException;
import com.controlfinance.common.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

  private final UserRepositoryPort users;
  private final PasswordEncoder encoder;
  private final JwtService jwt;
  private final TotpService totp;
  private final CryptoService crypto;

  public AuthTokensDto execute(LoginRequest req) {
    var user = users.findByEmail(req.getEmail().toLowerCase())
        .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

    if (!encoder.matches(req.getPassword(), user.getPasswordHash())) {
      throw new UnauthorizedException("Invalid credentials");
    }

    if (user.isTwoFactorEnabled()) {
      if (req.getTwoFactorCode() == null || req.getTwoFactorCode().isBlank()) {
        throw new BadRequestException("2FA code required");
      }
      String secret = crypto.decrypt(user.getTwoFactorSecretEnc());
      if (!totp.verifyCode(secret, req.getTwoFactorCode())) {
        throw new UnauthorizedException("Invalid 2FA code");
      }
    }

    user.setLastLoginAt(Instant.now());
    users.save(user);

    return AuthTokensDto.builder()
        .accessToken(jwt.generateAccessToken(user.getId(), user.getEmail(), user.getRole()))
        .refreshToken(jwt.generateRefreshToken(user.getId()))
        .build();
  }
}
