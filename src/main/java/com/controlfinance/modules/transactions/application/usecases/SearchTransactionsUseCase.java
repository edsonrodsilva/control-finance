package com.controlfinance.modules.transactions.application.usecases;

import com.controlfinance.common.security.SecurityUtils;
import com.controlfinance.modules.transactions.application.dto.TransactionDto;
import com.controlfinance.modules.transactions.application.mapper.TransactionMapper;
import com.controlfinance.modules.transactions.domain.repositories.TransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchTransactionsUseCase {

  private final TransactionRepositoryPort repo;
  private final TransactionMapper mapper;

  public List<TransactionDto> execute(Instant from, Instant to, String categoryId, String type, String status) {
    String userId = SecurityUtils.currentUserId();
    return repo.search(userId, from, to, categoryId, type, status).stream().map(mapper::toDto).toList();
  }
}
