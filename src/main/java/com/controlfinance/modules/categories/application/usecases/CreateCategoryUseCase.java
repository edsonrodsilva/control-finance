package com.controlfinance.modules.categories.application.usecases;

import com.controlfinance.infrastructure.security.SecurityUtils;
import com.controlfinance.modules.categories.application.dto.CategoryDto;
import com.controlfinance.modules.categories.application.mapper.CategoryMapper;
import com.controlfinance.modules.categories.domain.entities.Category;
import com.controlfinance.modules.categories.domain.repositories.CategoryRepositoryPort;
import com.controlfinance.shared.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCategoryUseCase {
  private final CategoryRepositoryPort repo;
  private final CategoryMapper mapper;

  public CategoryDto execute(CategoryDto dto) {
    if (dto.getName() == null || dto.getName().isBlank()) throw new BadRequestException("Category name required");
    if (dto.getType() == null || dto.getType().isBlank()) dto.setType("both");

    String userId = SecurityUtils.currentUserId();
    Category c = mapper.toEntity(dto);
    c.setId(null);
    c.setUserId(userId);
    c = repo.save(c);
    return mapper.toDto(c);
  }
}
