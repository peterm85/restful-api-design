package org.example.restful.adapter.repository.converter;

import org.example.restful.domain.Operation;
import org.example.restful.domain.OrderType;
import org.example.restful.port.repository.entity.OperationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OperationEntityToOperationConverter implements Converter<OperationEntity, Operation> {

  @Override
  public Operation convert(OperationEntity entity) {
    if (entity == null) {
      return null;
    } else {
      return Operation.builder()
          .id(entity.getId())
          .isin(entity.getStock().getIsin())
          .idNumber(entity.getInvestor().getIdNumber())
          .amount(entity.getAmount())
          .limitedPrize(entity.getLimitedPrize())
          .orderType(OrderType.valueOf(entity.getOrderTypeEntity().name()))
          .build();
    }
  }
}
