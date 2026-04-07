package com.controlfinance.modules.transactions.application.usecases;

import com.controlfinance.infrastructure.security.SecurityUtils;
import com.controlfinance.modules.transactions.application.dto.TransactionDto;
import com.controlfinance.modules.transactions.application.mapper.TransactionMapper;
import com.controlfinance.modules.transactions.domain.repositories.TransactionRepositoryPort;
import com.controlfinance.common.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateTransactionUseCase {

  private final TransactionRepositoryPort repo;
  private final TransactionMapper mapper;

  public TransactionDto execute(String id, TransactionDto dto) {
    String userId = SecurityUtils.currentUserId();
    var tx = repo.findByIdAndUserId(id, userId).orElseThrow(() -> new NotFoundException("Transaction not found"));

    if (dto.getCategoryId() != null) tx.setCategoryId(dto.getCategoryId());
    if (dto.getSubCategoryId() != null) tx.setSubCategoryId(dto.getSubCategoryId());
    if (dto.getAmount() != null) tx.setAmount(dto.getAmount());
    if (dto.getDescription() != null) tx.setDescription(dto.getDescription());
    if (dto.getStatus() != null) tx.setStatus(dto.getStatus());
    if (dto.getTransactionDate() != null) tx.setTransactionDate(dto.getTransactionDate());
    if (dto.getPaymentTicket() != null) tx.setPaymentTicket(dto.getPaymentTicket());
    if (dto.getType() != null) tx.setType(dto.getType());

    tx = repo.save(tx);
    return mapper.toDto(tx);
  }
}
