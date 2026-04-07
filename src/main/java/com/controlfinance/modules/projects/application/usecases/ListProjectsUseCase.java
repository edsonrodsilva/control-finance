package com.controlfinance.modules.projects.application.usecases;

import com.controlfinance.common.security.SecurityUtils;
import com.controlfinance.modules.projects.application.dto.ProjectDto;
import com.controlfinance.modules.projects.application.mapper.ProjectMapper;
import com.controlfinance.modules.projects.domain.repositories.ProjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListProjectsUseCase {

  private final ProjectRepositoryPort repo;
  private final ProjectMapper mapper;

  public List<ProjectDto> execute() {
    String userId = SecurityUtils.currentUserId();
    return repo.findAllByUserId(userId).stream().map(mapper::toDto).toList();
  }
}
