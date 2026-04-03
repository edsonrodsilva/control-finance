package com.controlfinance.modules.auth.application.usecases;

import com.controlfinance.infrastructure.security.JwtService;
import com.controlfinance.modules.auth.application.dto.AuthTokensDto;
import com.controlfinance.modules.auth.application.dto.RefreshRequest;
import com.controlfinance.modules.user.domain.repositories.UserRepositoryPort;
import com.controlfinance.shared.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

  private final JwtService jwt;
  private final UserRepositoryPort users;

  public AuthTokensDto execute(RefreshRequest req) {
    try {
      var jws = jwt.parseRefresh(req.getRefreshToken());
      String userId = jws.getBody().getSubject();
      var user = users.findById(userId).orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

      return AuthTokensDto.builder()
          .accessToken(jwt.generateAccessToken(user.getId(), user.getEmail(), user.getRole()))
          .refreshToken(jwt.generateRefreshToken(user.getId()))
          .build();
    } catch (Exception e) {
      throw new UnauthorizedException("Invalid refresh token");
    }
  }
}
