package com.controlfinance.modules.projects.infrastructure.persistence;

import com.controlfinance.modules.projects.domain.entities.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMongoRepository extends MongoRepository<Project, String> {
  Optional<Project> findByIdAndUserId(String id, String userId);
  List<Project> findAllByUserId(String userId);
}
