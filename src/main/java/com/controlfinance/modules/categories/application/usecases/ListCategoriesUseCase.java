package com.controlfinance.modules.categories.application.usecases;

import com.controlfinance.infrastructure.security.SecurityUtils;
import com.controlfinance.modules.categories.application.dto.CategoryDto;
import com.controlfinance.modules.categories.application.mapper.CategoryMapper;
import com.controlfinance.modules.categories.domain.repositories.CategoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListCategoriesUseCase {
  private final CategoryRepositoryPort repo;
  private final CategoryMapper mapper;

  public List<CategoryDto> execute() {
    String userId = SecurityUtils.currentUserId();
    return repo.findAllByUserId(userId).stream().map(mapper::toDto).toList();
  }
}
