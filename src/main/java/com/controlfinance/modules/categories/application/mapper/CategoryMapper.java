package com.controlfinance.modules.categories.application.mapper;

import com.controlfinance.modules.categories.application.dto.CategoryDto;
import com.controlfinance.modules.categories.domain.entities.Category;
import org.mapstruct.Mapper;

@Mapper
public interface CategoryMapper {
  CategoryDto toDto(Category c);
  Category toEntity(CategoryDto dto);
}
