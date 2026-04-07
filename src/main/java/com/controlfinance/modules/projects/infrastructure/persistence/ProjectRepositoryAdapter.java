package com.controlfinance.modules.projects.infrastructure.persistence;

import com.controlfinance.modules.projects.domain.entities.Project;
import com.controlfinance.modules.projects.domain.repositories.ProjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements ProjectRepositoryPort {

  private final ProjectMongoRepository repo;

  @Override
  @SuppressWarnings("null")
  public Project save(Project project) {
    return repo.save(project);
  }

  @Override
  public Optional<Project> findByIdAndUserId(String id, String userId) {
    return repo.findByIdAndUserId(id, userId);
  }

  @Override
  public List<Project> findAllByUserId(String userId) {
    return repo.findAllByUserId(userId);
  }

  @Override
  @SuppressWarnings("null")
  public void deleteById(String id) {
    repo.deleteById(id);
  }
}
