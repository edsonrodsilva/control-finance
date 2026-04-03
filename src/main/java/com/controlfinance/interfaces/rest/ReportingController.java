package com.controlfinance.interfaces.rest;

import com.controlfinance.modules.reporting.application.dto.DashboardSummaryDto;
import com.controlfinance.modules.reporting.application.usecases.GetDashboardSummaryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reporting")
@RequiredArgsConstructor
public class ReportingController {

  private final GetDashboardSummaryUseCase dashboard;

  @GetMapping("/dashboard")
  public ResponseEntity<DashboardSummaryDto> dashboard() {
    return ResponseEntity.ok(dashboard.execute());
  }
}
