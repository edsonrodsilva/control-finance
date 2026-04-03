package com.controlfinance.modules.budget.domain.repositories;

import com.controlfinance.modules.budget.domain.entities.Budget;

import java.util.List;
import java.util.Optional;

public interface BudgetRepositoryPort {
  Budget save(Budget b);
  Optional<Budget> findByUserIdAndYearMonthAndCategoryId(String userId, String yearMonth, String categoryId);
  List<Budget> findByUserIdAndYearMonth(String userId, String yearMonth);
}
