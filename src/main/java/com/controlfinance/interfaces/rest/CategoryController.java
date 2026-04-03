package com.controlfinance.interfaces.rest;

import com.controlfinance.modules.categories.application.dto.CategoryDto;
import com.controlfinance.modules.categories.application.usecases.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CreateCategoryUseCase create;
  private final ListCategoriesUseCase list;
  private final UpdateCategoryUseCase update;
  private final DeleteCategoryUseCase delete;

  @PostMapping
  public ResponseEntity<CategoryDto> create(@Valid @RequestBody CategoryDto dto) {
    return ResponseEntity.ok(create.execute(dto));
  }

  @GetMapping
  public ResponseEntity<List<CategoryDto>> list() {
    return ResponseEntity.ok(list.execute());
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryDto> update(@PathVariable String id, @RequestBody CategoryDto dto) {
    return ResponseEntity.ok(update.execute(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    delete.execute(id);
    return ResponseEntity.noContent().build();
  }
}
