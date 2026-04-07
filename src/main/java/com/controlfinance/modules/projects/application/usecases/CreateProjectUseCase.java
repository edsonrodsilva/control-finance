package com.controlfinance.modules.projects.application.usecases;

import com.controlfinance.common.events.DomainEventPublisher;
import com.controlfinance.common.exceptions.BadRequestException;
import com.controlfinance.common.security.SecurityUtils;
import com.controlfinance.modules.projects.application.dto.ProjectDto;
import com.controlfinance.modules.projects.application.mapper.ProjectMapper;
import com.controlfinance.modules.projects.domain.entities.Project;
import com.controlfinance.modules.projects.domain.enums.ProjectStatus;
import com.controlfinance.modules.projects.domain.events.ProjectCreatedEvent;
import com.controlfinance.modules.projects.domain.repositories.ProjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CreateProjectUseCase {

  private final ProjectRepositoryPort repo;
  private final ProjectMapper mapper;
  private final DomainEventPublisher events;

  public ProjectDto execute(ProjectDto dto) {
    if (dto.getGoalAmount() == null || dto.getGoalAmount().signum() <= 0) {
      throw new BadRequestException("Goal amount must be positive");
    }

    String userId = SecurityUtils.currentUserId();
    Project project = mapper.toEntity(dto);
    project.setId(null);
    project.setUserId(userId);
    project.setStatus(ProjectStatus.ACTIVE);
    project.setCurrentAmount(BigDecimal.ZERO);

    project = repo.save(project);

    events.publish(new ProjectCreatedEvent(
        project.getId(),
        project.getUserId(),
        Instant.now(),
        project.getId(),
        project.getName(),
        project.getGoalAmount()
    ));

    return mapper.toDto(project);
  }
}
