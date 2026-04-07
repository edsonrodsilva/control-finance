package com.controlfinance.modules.projects.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.controlfinance.modules.projects.domain.entities.Project;

public interface ProjectRepositoryPort {
    Project save(Project project);
    Optional<Project> findByIdAndUserId(String id, String userId);
    List<Project> findAllByUserId(String userId);
    void deleteById(String id);
}
