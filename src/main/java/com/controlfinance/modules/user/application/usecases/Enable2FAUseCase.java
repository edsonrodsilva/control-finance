package com.controlfinance.modules.user.application.usecases;

import com.controlfinance.infrastructure.security.CryptoService;
import com.controlfinance.common.security.SecurityUtils;
import com.controlfinance.modules.user.domain.repositories.UserRepositoryPort;
import com.controlfinance.modules.user.domain.services.TotpService;
import com.controlfinance.common.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Enable2FAUseCase {
  private final UserRepositoryPort users;
  private final TotpService totp;
  private final CryptoService crypto;

  /** Gera e persiste um segredo (criptografado). Retorna o segredo (para QR). */
  public String generateSecret() {
    String userId = SecurityUtils.currentUserId();
    var user = users.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

    String secret = totp.generateSecret();
    user.setTwoFactorSecretEnc(crypto.encrypt(secret));
    user.setTwoFactorEnabled(false);
    users.save(user);
    return secret;
  }

  /** Confirma o 2FA validando um código. */
  public void confirm(String code) {
    String userId = SecurityUtils.currentUserId();
    var user = users.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    String secret = crypto.decrypt(user.getTwoFactorSecretEnc());

    if (totp.verifyCode(secret, code)) {
      user.setTwoFactorEnabled(true);
      users.save(user);
    } else {
      throw new com.controlfinance.common.exceptions.BadRequestException("Invalid 2FA code");
    }
  }
}
