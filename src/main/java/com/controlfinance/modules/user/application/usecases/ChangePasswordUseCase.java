package com.controlfinance.modules.user.application.usecases;

import com.controlfinance.infrastructure.security.SecurityUtils;
import com.controlfinance.modules.user.domain.repositories.UserRepositoryPort;
import com.controlfinance.shared.exceptions.BadRequestException;
import com.controlfinance.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangePasswordUseCase {
  private final UserRepositoryPort users;
  private final PasswordEncoder encoder;

  public void execute(String currentPassword, String newPassword) {
    if (newPassword == null || newPassword.length() < 8) throw new BadRequestException("New password must have at least 8 chars");

    String userId = SecurityUtils.currentUserId();
    var user = users.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

    if (!encoder.matches(currentPassword, user.getPasswordHash())) {
      throw new BadRequestException("Current password does not match");
    }

    user.setPasswordHash(encoder.encode(newPassword));
    users.save(user);
  }
}
