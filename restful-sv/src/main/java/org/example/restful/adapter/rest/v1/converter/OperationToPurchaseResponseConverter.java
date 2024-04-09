package org.example.restful.adapter.rest.v1.converter;

import org.example.restful.domain.Operation;
import org.example.restful.port.rest.v1.api.model.OrderTypeRequest;
import org.example.restful.port.rest.v1.api.model.PurchaseResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OperationToPurchaseResponseConverter
    implements Converter<Operation, PurchaseResponse> {

  @Override
  public PurchaseResponse convert(Operation domain) {
    if (domain == null) {
      return null;
    } else {
      return PurchaseResponse.builder()
          .id(domain.getId())
          .isin(domain.getIsin())
          .idNumber(domain.getIdNumber())
          .amount(domain.getAmount())
          .limitedPrize(domain.getLimitedPrize())
          .orderType(OrderTypeRequest.valueOf(domain.getOrderType().name()))
          .build();
    }
  }
}
