package com.controlfinance.modules.transactions.application.mapper;

import com.controlfinance.modules.transactions.application.dto.TransactionDto;
import com.controlfinance.modules.transactions.domain.entities.Transaction;
import org.mapstruct.Mapper;

@Mapper
public interface TransactionMapper {
  TransactionDto toDto(Transaction t);
  Transaction toEntity(TransactionDto dto);
}
