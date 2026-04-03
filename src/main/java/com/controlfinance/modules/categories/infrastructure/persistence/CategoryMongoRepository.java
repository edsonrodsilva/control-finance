package com.controlfinance.modules.categories.infrastructure.persistence;

import com.controlfinance.modules.categories.domain.entities.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryMongoRepository extends MongoRepository<Category, String> {
  Optional<Category> findByIdAndUserId(String id, String userId);
  List<Category> findAllByUserId(String userId);
}
