package com.controlfinance.modules.projects.application.usecases;

import com.controlfinance.common.exceptions.NotFoundException;
import com.controlfinance.common.security.SecurityUtils;
import com.controlfinance.modules.projects.domain.repositories.ProjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteProjectUseCase {

  private final ProjectRepositoryPort repo;

  public void execute(String id) {
    String userId = SecurityUtils.currentUserId();
    repo.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new NotFoundException("Project not found"));

    repo.deleteById(id);
  }
}
