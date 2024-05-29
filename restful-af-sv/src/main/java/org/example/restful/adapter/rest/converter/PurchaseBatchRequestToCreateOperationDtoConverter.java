package org.example.restful.adapter.rest.converter;

import org.example.restful.domain.OperationType;
import org.example.restful.domain.OrderType;
import org.example.restful.domain.dtos.CreateOperationDto;
import org.openapitools.model.PurchaseBatchRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PurchaseBatchRequestToCreateOperationDtoConverter
    implements Converter<PurchaseBatchRequest, CreateOperationDto> {

  @Override
  public CreateOperationDto convert(PurchaseBatchRequest request) {
    if (request == null) {
      return null;
    } else {
      return CreateOperationDto.builder()
          .amount(request.getAmount())
          .limitedPrize(request.getLimitedPrize())
          .orderType(OrderType.valueOf(request.getOrderType()))
          .operationType(OperationType.BUY)
          .build();
    }
  }
}
