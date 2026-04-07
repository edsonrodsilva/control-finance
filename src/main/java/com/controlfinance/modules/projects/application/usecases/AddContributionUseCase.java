package com.controlfinance.modules.projects.application.usecases;

import com.controlfinance.common.events.DomainEventPublisher;
import com.controlfinance.common.exceptions.BadRequestException;
import com.controlfinance.common.exceptions.NotFoundException;
import com.controlfinance.common.security.SecurityUtils;
import com.controlfinance.modules.projects.application.dto.AddContributionDto;
import com.controlfinance.modules.projects.application.dto.ProjectDto;
import com.controlfinance.modules.projects.application.mapper.ProjectMapper;
import com.controlfinance.modules.projects.domain.enums.ProjectStatus;
import com.controlfinance.modules.projects.domain.events.ProjectGoalReachedEvent;
import com.controlfinance.modules.projects.domain.repositories.ProjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AddContributionUseCase {

  private final ProjectRepositoryPort repo;
  private final ProjectMapper mapper;
  private final DomainEventPublisher events;

  public ProjectDto execute(String id, AddContributionDto dto) {
    String userId = SecurityUtils.currentUserId();
    var project = repo.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new NotFoundException("Project not found"));

    if (project.getStatus() != ProjectStatus.ACTIVE) {
      throw new BadRequestException("Only ACTIVE projects accept contributions");
    }

    if (dto.getAmount() == null || dto.getAmount().signum() <= 0) {
      throw new BadRequestException("Contribution amount must be positive");
    }

    BigDecimal current = project.getCurrentAmount() == null ? BigDecimal.ZERO : project.getCurrentAmount();
    project.setCurrentAmount(current.add(dto.getAmount()));

    if (project.getCurrentAmount().compareTo(project.getGoalAmount()) >= 0) {
      project.setStatus(ProjectStatus.COMPLETED);

      events.publish(new ProjectGoalReachedEvent(
          project.getId(),
          project.getUserId(),
          Instant.now(),
          project.getId(),
          project.getName()
      ));
    }

    project = repo.save(project);
    return mapper.toDto(project);
  }
}
