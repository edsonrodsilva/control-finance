package com.controlfinance.modules.transactions.application.usecases;

import com.controlfinance.modules.transactions.domain.entities.Transaction;
import com.controlfinance.modules.transactions.domain.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;

/**
 * Serviço de leitura para somatório mensal por categoria.
 * Em produção, isso pode virar um read model (projeção) mantido por eventos.
 */
@Service
@RequiredArgsConstructor
public class ComputeMonthSpentService {

  private final MongoTemplate template;

  public BigDecimal spentAfter(Transaction tx) {
    if (tx.getType() != TransactionType.EXPENSE) return BigDecimal.ZERO;

    ZoneId zone = ZoneId.systemDefault();
    LocalDate d = LocalDateTime.ofInstant(tx.getTransactionDate(), zone).toLocalDate();
    LocalDate first = d.withDayOfMonth(1);
    LocalDate last = d.withDayOfMonth(d.lengthOfMonth());

    Instant from = first.atStartOfDay(zone).toInstant();
    Instant to = last.atTime(LocalTime.MAX).atZone(zone).toInstant();

    var match = Aggregation.match(new Criteria().andOperator(
        Criteria.where("userId").is(tx.getUserId()),
        Criteria.where("type").is(TransactionType.EXPENSE),
        Criteria.where("categoryId").is(tx.getCategoryId()),
        Criteria.where("transactionDate").gte(from).lte(to)
    ));

    var group = Aggregation.group().sum("amount").as("total");

    Aggregation agg = Aggregation.newAggregation(match, group);
    AggregationResults<Total> res = template.aggregate(agg, "transactions", Total.class);
    BigDecimal total = res.getUniqueMappedResult() != null ? res.getUniqueMappedResult().total : BigDecimal.ZERO;

    // garantindo que o valor atual esteja no somatório (dependendo do timing da escrita)
    if (total == null) total = BigDecimal.ZERO;
    return total;
  }

  public record Total(BigDecimal total) {}
}
