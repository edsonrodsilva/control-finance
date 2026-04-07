package com.controlfinance.modules.projects.application.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.controlfinance.modules.projects.domain.enums.ProjectStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProjectDto {
    private String id;

    @NotBlank
    private String name;

    private String description;

    @NotNull @Positive
    private BigDecimal goalAmount;

    private BigDecimal currentAmount;

    private ProjectStatus status;

    @NotNull
    private Instant targetDate;
}