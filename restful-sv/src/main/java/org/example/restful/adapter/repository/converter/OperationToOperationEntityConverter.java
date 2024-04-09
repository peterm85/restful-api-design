package org.example.restful.adapter.repository.converter;

import org.example.restful.adapter.repository.entity.OperationEntity;
import org.example.restful.adapter.repository.entity.OperationTypeEntity;
import org.example.restful.adapter.repository.entity.OrderTypeEntity;
import org.example.restful.domain.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OperationToOperationEntityConverter {

  @Autowired private InvestorToInvestorEntityConverter investorToEntityConverter;
  @Autowired private StockToStockEntityConverter stockToEntityConverter;

  public OperationEntity convert(Operation domain) {
    if (domain == null) {
      return null;
    } else {
      return OperationEntity.builder()
          .investor(investorToEntityConverter.convert(domain.getInvestor()))
          .stock(stockToEntityConverter.convert(domain.getStock()))
          .creationDateTime(Instant.now())
          .amount(domain.getAmount())
          .limitedPrize(domain.getLimitedPrize())
          .orderTypeEntity(OrderTypeEntity.valueOf(domain.getOrderType().name()))
          .operationTypeEntity(OperationTypeEntity.valueOf(domain.getOperationType().name()))
          .build();
    }
  }
}
