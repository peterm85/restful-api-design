package org.example.restful.adapter.rest.v1.converter;

import org.example.restful.domain.Operation;
import org.openapitools.model.PurchaseResponse;
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
          .operationId(domain.getId())
          .isin(domain.getStock().getIsin())
          .idNumber(domain.getInvestor().getIdNumber())
          .amount(domain.getAmount())
          .limitedPrize(domain.getLimitedPrize())
          .orderType(domain.getOrderType().name())
          .build();
    }
  }
}
