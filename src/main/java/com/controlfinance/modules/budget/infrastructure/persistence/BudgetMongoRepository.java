package com.controlfinance.modules.budget.infrastructure.persistence;

import com.controlfinance.modules.budget.domain.entities.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetMongoRepository extends MongoRepository<Budget, String> {
  Optional<Budget> findByUserIdAndYearMonthAndCategoryId(String userId, String yearMonth, String categoryId);
  List<Budget> findByUserIdAndYearMonth(String userId, String yearMonth);
}
