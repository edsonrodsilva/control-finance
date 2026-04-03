package com.controlfinance.modules.budget.infrastructure.persistence;

import com.controlfinance.modules.budget.domain.entities.Budget;
import com.controlfinance.modules.budget.domain.repositories.BudgetRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BudgetRepositoryAdapter implements BudgetRepositoryPort {
  private final BudgetMongoRepository repo;

  @Override
  public Budget save(Budget b) { return repo.save(b); }

  @Override
  public Optional<Budget> findByUserIdAndYearMonthAndCategoryId(String userId, String yearMonth, String categoryId) {
    return repo.findByUserIdAndYearMonthAndCategoryId(userId, yearMonth, categoryId);
  }

  @Override
  public List<Budget> findByUserIdAndYearMonth(String userId, String yearMonth) {
    return repo.findByUserIdAndYearMonth(userId, yearMonth);
  }
}
