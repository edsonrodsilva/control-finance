package com.controlfinance.modules.categories.application.usecases;

import com.controlfinance.common.security.SecurityUtils;
import com.controlfinance.modules.categories.application.dto.CategoryDto;
import com.controlfinance.modules.categories.application.mapper.CategoryMapper;
import com.controlfinance.modules.categories.domain.repositories.CategoryRepositoryPort;
import com.controlfinance.common.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateCategoryUseCase {
  private final CategoryRepositoryPort repo;
  private final CategoryMapper mapper;

  public CategoryDto execute(String id, CategoryDto dto) {
    String userId = SecurityUtils.currentUserId();
    var c = repo.findByIdAndUserId(id, userId).orElseThrow(() -> new NotFoundException("Category not found"));
    if (dto.getName() != null && !dto.getName().isBlank()) c.setName(dto.getName().trim());
    if (dto.getType() != null && !dto.getType().isBlank()) c.setType(dto.getType());
    c = repo.save(c);
    return mapper.toDto(c);
  }
}
