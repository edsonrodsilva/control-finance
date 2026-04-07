package com.controlfinance.modules.projects.domain.entities;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Document;

import com.controlfinance.common.base.BaseDocument;
import com.controlfinance.modules.projects.domain.enums.ProjectStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Document(collection = "projects")
public class Project extends BaseDocument {
    @NotBlank
    private String name;

    private String description;

    /** Meta financeira total do projeto */
    @Builder.Default
    private BigDecimal goalAmount = BigDecimal.ZERO;

    /** Valor ja acumulado / contribuído */
    @Builder.Default
    private BigDecimal currentAmount = BigDecimal.ZERO;

     /** Status do projeto */
     @Builder.Default
     private ProjectStatus status = ProjectStatus.ACTIVE;

     /** Data-alvo para atingir a meta financeira */
     private Instant targetDate;

}
