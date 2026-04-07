package com.controlfinance.api.rest;

import com.controlfinance.modules.transactions.application.dto.TransactionDto;
import com.controlfinance.modules.transactions.application.usecases.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

  private final CreateTransactionUseCase create;
  private final UpdateTransactionUseCase update;
  private final DeleteTransactionUseCase delete;
  private final SearchTransactionsUseCase search;

  @PostMapping
  public ResponseEntity<TransactionDto> create(@Valid @RequestBody TransactionDto dto) {
    return ResponseEntity.ok(create.execute(dto));
  }

  @GetMapping
  public ResponseEntity<List<TransactionDto>> search(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
      @RequestParam(required = false) String categoryId,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String status
  ) {
    return ResponseEntity.ok(search.execute(from, to, categoryId, type, status));
  }

  @PutMapping("/{id}")
  public ResponseEntity<TransactionDto> update(@PathVariable String id, @RequestBody TransactionDto dto) {
    return ResponseEntity.ok(update.execute(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    delete.execute(id);
    return ResponseEntity.noContent().build();
  }
}
