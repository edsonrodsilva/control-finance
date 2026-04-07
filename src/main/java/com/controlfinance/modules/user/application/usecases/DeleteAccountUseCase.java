package com.controlfinance.modules.user.application.usecases;

import com.controlfinance.common.security.SecurityUtils;
import com.controlfinance.modules.user.domain.repositories.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteAccountUseCase {
  private final UserRepositoryPort users;

  public void execute() {
    String userId = SecurityUtils.currentUserId();
    users.deleteById(userId);
  }
}
