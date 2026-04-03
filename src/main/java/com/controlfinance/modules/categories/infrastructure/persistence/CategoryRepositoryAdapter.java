package com.controlfinance.modules.categories.infrastructure.persistence;

import com.controlfinance.modules.categories.domain.entities.Category;
import com.controlfinance.modules.categories.domain.repositories.CategoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {
  private final CategoryMongoRepository repo;

  @Override
  public Category save(Category c) { return repo.save(c); }

  @Override
  public Optional<Category> findByIdAndUserId(String id, String userId) { return repo.findByIdAndUserId(id, userId); }

  @Override
  public List<Category> findAllByUserId(String userId) { return repo.findAllByUserId(userId); }

  @Override
  public void deleteById(String id) { repo.deleteById(id); }
}
