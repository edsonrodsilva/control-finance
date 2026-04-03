package com.controlfinance.modules.user.domain.repositories;

import com.controlfinance.modules.user.domain.entities.User;

import java.util.Optional;

public interface UserRepositoryPort {
  User save(User user);
  Optional<User> findById(String id);
  Optional<User> findByEmail(String email);
  Optional<User> findByCpf(String cpf);
  void deleteById(String id);
}
