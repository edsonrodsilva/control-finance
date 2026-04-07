package com.controlfinance.modules.projects.application.usecases;

import com.controlfinance.common.exceptions.BadRequestException;
import com.controlfinance.common.exceptions.NotFoundException;
import com.controlfinance.common.security.SecurityUtils;
import com.controlfinance.modules.projects.application.dto.ProjectDto;
import com.controlfinance.modules.projects.application.mapper.ProjectMapper;
import com.controlfinance.modules.projects.domain.repositories.ProjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateProjectUseCase {

  private final ProjectRepositoryPort repo;
  private final ProjectMapper mapper;

  public ProjectDto execute(String id, ProjectDto dto) {
    String userId = SecurityUtils.currentUserId();
    var project = repo.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new NotFoundException("Project not found"));

    if (dto.getGoalAmount() != null && dto.getGoalAmount().signum() <= 0) {
      throw new BadRequestException("Goal amount must be positive");
    }

    if (dto.getName() != null) project.setName(dto.getName());
    if (dto.getDescription() != null) project.setDescription(dto.getDescription());
    if (dto.getGoalAmount() != null) project.setGoalAmount(dto.getGoalAmount());
    if (dto.getTargetDate() != null) project.setTargetDate(dto.getTargetDate());
    if (dto.getStatus() != null) project.setStatus(dto.getStatus());

    project = repo.save(project);
    return mapper.toDto(project);
  }
}
