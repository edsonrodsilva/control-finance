package com.controlfinance.modules.projects.application.mapper;

import com.controlfinance.modules.projects.application.dto.ProjectDto;
import com.controlfinance.modules.projects.domain.entities.Project;
import org.mapstruct.Mapper;

@Mapper
public interface ProjectMapper {
  ProjectDto toDto(Project project);
  Project toEntity(ProjectDto dto);
}
