package com.controlfinance.api.rest;

import com.controlfinance.modules.projects.application.dto.AddContributionDto;
import com.controlfinance.modules.projects.application.dto.ProjectDto;
import com.controlfinance.modules.projects.application.usecases.AddContributionUseCase;
import com.controlfinance.modules.projects.application.usecases.CreateProjectUseCase;
import com.controlfinance.modules.projects.application.usecases.DeleteProjectUseCase;
import com.controlfinance.modules.projects.application.usecases.ListProjectsUseCase;
import com.controlfinance.modules.projects.application.usecases.UpdateProjectUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

  private final CreateProjectUseCase create;
  private final ListProjectsUseCase list;
  private final UpdateProjectUseCase update;
  private final DeleteProjectUseCase delete;
  private final AddContributionUseCase addContribution;

  @PostMapping
  public ResponseEntity<ProjectDto> create(@Valid @RequestBody ProjectDto dto) {
    return ResponseEntity.ok(create.execute(dto));
  }

  @GetMapping
  public ResponseEntity<List<ProjectDto>> list() {
    return ResponseEntity.ok(list.execute());
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProjectDto> update(@PathVariable String id, @RequestBody ProjectDto dto) {
    return ResponseEntity.ok(update.execute(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    delete.execute(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/contributions")
  public ResponseEntity<ProjectDto> addContribution(@PathVariable String id, @Valid @RequestBody AddContributionDto dto) {
    return ResponseEntity.ok(addContribution.execute(id, dto));
  }
}
