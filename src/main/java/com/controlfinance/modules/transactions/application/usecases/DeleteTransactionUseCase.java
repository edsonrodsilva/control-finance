package com.controlfinance.modules.transactions.application.usecases;

import com.controlfinance.common.security.SecurityUtils;
import com.controlfinance.modules.transactions.domain.repositories.TransactionRepositoryPort;
import com.controlfinance.common.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteTransactionUseCase {

  private final TransactionRepositoryPort repo;

  public void execute(String id) {
    String userId = SecurityUtils.currentUserId();
    repo.findByIdAndUserId(id, userId).orElseThrow(() -> new NotFoundException("Transaction not found"));
    repo.deleteById(id);
  }
}
