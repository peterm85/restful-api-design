package org.example.restful.adapter.repository.converter;

import org.example.restful.adapter.repository.entity.OperationEntity;
import org.example.restful.domain.Operation;
import org.example.restful.domain.OrderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OperationEntityToOperationConverter implements Converter<OperationEntity, Operation> {

  @Autowired private InvestorEntityToInvestorConverter investorToDomainConverter;
  @Autowired private StockEntityToStockConverter stockToDomainConverter;

  @Override
  public Operation convert(OperationEntity entity) {
    if (entity == null) {
      return null;
    } else {
      return Operation.builder()
          .id(entity.getId())
          .stock(stockToDomainConverter.convert(entity.getStock()))
          .investor(investorToDomainConverter.convert(entity.getInvestor()))
          .amount(entity.getAmount())
          .limitedPrize(entity.getLimitedPrize())
          .orderType(OrderType.valueOf(entity.getOrderTypeEntity().name()))
          .build();
    }
  }
}
