package com.controlfinance.modules.auth.application.usecases;

import com.controlfinance.common.events.DomainEventPublisher;
import com.controlfinance.infrastructure.security.AppRoles;
import com.controlfinance.modules.auth.application.dto.AuthTokensDto;
import com.controlfinance.modules.auth.application.dto.RegisterRequest;
import com.controlfinance.modules.auth.domain.events.UserRegisteredEvent;
import com.controlfinance.modules.user.domain.entities.User;
import com.controlfinance.modules.user.domain.repositories.UserRepositoryPort;
import com.controlfinance.common.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

  private final UserRepositoryPort users;
  private final PasswordEncoder encoder;
  private final com.controlfinance.infrastructure.security.JwtService jwt;
  private final DomainEventPublisher events;

  public AuthTokensDto execute(RegisterRequest req) {
    users.findByEmail(req.getEmail().toLowerCase()).ifPresent(u -> {
      throw new BadRequestException("Email already registered");
    });
    users.findByCpf(req.getCpf()).ifPresent(u -> {
      throw new BadRequestException("CPF already registered");
    });

    User user = User.builder()
        .name(req.getName().trim())
        .email(req.getEmail().toLowerCase())
        .cpf(req.getCpf())
        .passwordHash(encoder.encode(req.getPassword()))
        .role(AppRoles.USER)
        .twoFactorEnabled(false)
        .build();

    user = users.save(user);

    events.publish(new UserRegisteredEvent(user.getId(), user.getId(), Instant.now(), user.getEmail()));

    return AuthTokensDto.builder()
        .accessToken(jwt.generateAccessToken(user.getId(), user.getEmail(), user.getRole()))
        .refreshToken(jwt.generateRefreshToken(user.getId()))
        .build();
  }
}
