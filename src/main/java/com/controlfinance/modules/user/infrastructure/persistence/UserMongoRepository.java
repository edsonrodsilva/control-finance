package com.controlfinance.modules.user.infrastructure.persistence;

import com.controlfinance.modules.user.domain.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserMongoRepository extends MongoRepository<User, String> {
  Optional<User> findByEmail(String email);
  Optional<User> findByCpf(String cpf);
}
