package com.controlfinance.modules.reporting.application.usecases;

import com.controlfinance.infrastructure.security.SecurityUtils;
import com.controlfinance.modules.reporting.application.dto.DashboardSummaryDto;
import com.controlfinance.modules.transactions.domain.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class GetDashboardSummaryUseCase {

  private final MongoTemplate template;

  public DashboardSummaryDto execute() {
    String userId = SecurityUtils.currentUserId();

    BigDecimal income = sumByType(userId, TransactionType.INCOME);
    BigDecimal expense = sumByType(userId, TransactionType.EXPENSE);

    return DashboardSummaryDto.builder()
        .totalIncome(income)
        .totalExpense(expense)
        .balance(income.subtract(expense))
        .build();
  }

  private BigDecimal sumByType(String userId, TransactionType type) {
    var match = Aggregation.match(new Criteria().andOperator(
        Criteria.where("userId").is(userId),
        Criteria.where("type").is(type)
    ));
    var group = Aggregation.group().sum("amount").as("total");
    var agg = Aggregation.newAggregation(match, group);
    var res = template.aggregate(agg, "transactions", Total.class);
    return res.getUniqueMappedResult() != null && res.getUniqueMappedResult().total != null
        ? res.getUniqueMappedResult().total
        : BigDecimal.ZERO;
  }

  public record Total(BigDecimal total) {}
}
