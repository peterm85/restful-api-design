package org.example.restful.adapter.rest.converter;

import org.example.restful.domain.OperationType;
import org.example.restful.domain.OrderType;
import org.example.restful.domain.dtos.CreateOperationDto;
import org.openapitools.model.PurchaseRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PurchaseRequestToCreateOperationDtoConverter
    implements Converter<PurchaseRequest, CreateOperationDto> {

  @Override
  public CreateOperationDto convert(PurchaseRequest request) {
    if (request == null) {
      return null;
    } else {
      return CreateOperationDto.builder()
          .amount(request.getAmount())
          .limitedPrize(request.getLimitedPrize().doubleValue())
          .orderType(OrderType.valueOf(request.getOrderType()))
          .operationType(OperationType.BUY)
          .build();
    }
  }
}
