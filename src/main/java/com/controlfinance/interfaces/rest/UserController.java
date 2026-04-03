package com.controlfinance.interfaces.rest;

import com.controlfinance.modules.user.application.dto.UserDto;
import com.controlfinance.modules.user.application.usecases.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final GetMyProfileUseCase getMyProfile;
  private final UpdateProfileUseCase updateProfile;
  private final ChangePasswordUseCase changePassword;
  private final DeleteAccountUseCase deleteAccount;
  private final com.controlfinance.modules.user.application.usecases.Enable2FAUseCase enable2fa;

  @GetMapping("/me")
  public ResponseEntity<UserDto> me() {
    return ResponseEntity.ok(getMyProfile.execute());
  }

  @PatchMapping("/me")
  public ResponseEntity<UserDto> update(@RequestBody UpdateProfileRequest req) {
    return ResponseEntity.ok(updateProfile.execute(req.name));
  }

  @PostMapping("/me/change-password")
  public ResponseEntity<Void> change(@RequestBody ChangePasswordRequest req) {
    changePassword.execute(req.currentPassword, req.newPassword);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/me")
  public ResponseEntity<Void> delete() {
    deleteAccount.execute();
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/me/2fa/secret")
  public ResponseEntity<SecretResponse> create2faSecret() {
    return ResponseEntity.ok(new SecretResponse(enable2fa.generateSecret()));
  }

  @PostMapping("/me/2fa/confirm")
  public ResponseEntity<Void> confirm2fa(@RequestBody Confirm2FARequest req) {
    enable2fa.confirm(req.code);
    return ResponseEntity.noContent().build();
  }

  public record UpdateProfileRequest(@NotBlank String name) {}
  public record ChangePasswordRequest(@NotBlank String currentPassword, @NotBlank String newPassword) {}
  public record Confirm2FARequest(@NotBlank String code) {}
  public record SecretResponse(String secret) {}
}
