package org.example.restful.adapter.rest.v1.converter;

import org.example.restful.domain.Operation;
import org.example.restful.domain.OperationType;
import org.example.restful.domain.OrderType;
import org.example.restful.port.rest.v1.api.model.PurchaseRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PurchaseRequestToOperationConverter implements Converter<PurchaseRequest, Operation> {

  @Override
  public Operation convert(PurchaseRequest request) {
    if (request == null) {
      return null;
    } else {
      return Operation.builder()
          .isin(request.getIsin())
          .amount(request.getAmount())
          .limitedPrize(request.getLimitedPrize())
          .orderType(OrderType.valueOf(request.getOrderType().name()))
          .operationType(OperationType.BUY)
          .build();
    }
  }
}
