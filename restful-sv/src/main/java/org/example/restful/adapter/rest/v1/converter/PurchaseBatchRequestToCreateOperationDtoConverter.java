package org.example.restful.adapter.rest.v1.converter;

import org.example.restful.domain.OperationType;
import org.example.restful.domain.OrderType;
import org.example.restful.domain.dtos.CreateOperationDto;
import org.example.restful.port.rest.v1.api.model.PurchaseBatchRequest;
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
          .orderType(OrderType.valueOf(request.getOrderType().name()))
          .operationType(OperationType.BUY)
          .build();
    }
  }
}
