package com.controlfinance.modules.categories.application.usecases;

import com.controlfinance.infrastructure.security.SecurityUtils;
import com.controlfinance.modules.categories.domain.repositories.CategoryRepositoryPort;
import com.controlfinance.common.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteCategoryUseCase {
  private final CategoryRepositoryPort repo;

  public void execute(String id) {
    String userId = SecurityUtils.currentUserId();
    repo.findByIdAndUserId(id, userId).orElseThrow(() -> new NotFoundException("Category not found"));
    repo.deleteById(id);
  }
}
