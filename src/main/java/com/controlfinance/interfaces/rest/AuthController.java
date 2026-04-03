package com.controlfinance.interfaces.rest;

import com.controlfinance.modules.auth.application.dto.*;
import com.controlfinance.modules.auth.application.usecases.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final RegisterUserUseCase register;
  private final LoginUseCase login;
  private final RefreshTokenUseCase refresh;

  @PostMapping("/register")
  public ResponseEntity<AuthTokensDto> register(@Valid @RequestBody RegisterRequest req) {
    return ResponseEntity.ok(register.execute(req));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthTokensDto> login(@Valid @RequestBody LoginRequest req) {
    return ResponseEntity.ok(login.execute(req));
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthTokensDto> refresh(@Valid @RequestBody RefreshRequest req) {
    return ResponseEntity.ok(refresh.execute(req));
  }
}
