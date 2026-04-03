package com.controlfinance.modules.user.application.usecases;

import com.controlfinance.infrastructure.security.SecurityUtils;
import com.controlfinance.modules.user.application.dto.UserDto;
import com.controlfinance.modules.user.application.mapper.UserMapper;
import com.controlfinance.modules.user.domain.repositories.UserRepositoryPort;
import com.controlfinance.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetMyProfileUseCase {
  private final UserRepositoryPort users;
  private final UserMapper mapper;

  public UserDto execute() {
    String userId = SecurityUtils.currentUserId();
    var user = users.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    return mapper.toDto(user);
  }
}
