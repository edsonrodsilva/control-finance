package com.controlfinance.modules.user.infrastructure.persistence;

import com.controlfinance.modules.user.domain.entities.User;
import com.controlfinance.modules.user.domain.repositories.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

  private final UserMongoRepository repo;

  @Override
  public User save(User user) { return repo.save(user); }

  @Override
  public Optional<User> findById(String id) { return repo.findById(id); }

  @Override
  public Optional<User> findByEmail(String email) { return repo.findByEmail(email); }

  @Override
  public Optional<User> findByCpf(String cpf) { return repo.findByCpf(cpf); }

  @Override
  public void deleteById(String id) { repo.deleteById(id); }
}
