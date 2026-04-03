package com.controlfinance.modules.categories.domain.repositories;

import com.controlfinance.modules.categories.domain.entities.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {
  Category save(Category c);
  Optional<Category> findByIdAndUserId(String id, String userId);
  List<Category> findAllByUserId(String userId);
  void deleteById(String id);
}
