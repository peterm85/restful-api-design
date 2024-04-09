package org.example.restful.adapter.repository.converter;

import java.time.Instant;

import org.example.restful.domain.Investor;
import org.example.restful.domain.Operation;
import org.example.restful.domain.Stock;
import org.example.restful.port.repository.entity.OperationEntity;
import org.example.restful.port.repository.entity.OperationTypeEntity;
import org.example.restful.port.repository.entity.OrderTypeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OperationToOperationEntityConverter {

  @Autowired private InvestorToInvestorEntityConverter investorToEntityConverter;
  @Autowired private StockToStockEntityConverter stockToEntityConverter;

  public OperationEntity convert(Operation domain, Investor investor, Stock stock) {
    if (domain == null) {
      return null;
    } else {
      return OperationEntity.builder()
          .investor(investorToEntityConverter.convert(investor))
          .stock(stockToEntityConverter.convert(stock))
          .creationDateTime(Instant.now())
          .amount(domain.getAmount())
          .limitedPrize(domain.getLimitedPrize())
          .orderTypeEntity(OrderTypeEntity.valueOf(domain.getOrderType().name()))
          .operationTypeEntity(OperationTypeEntity.valueOf(domain.getOperationType().name()))
          .build();
    }
  }
}
