package org.example.restful.domain;

import org.example.restful.domain.dtos.CreateOperationDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Operation {
  private Long id;
  private Stock stock;
  private Investor investor;
  private Integer amount;
  private Double limitedPrize;
  private OrderType orderType;
  private OperationType operationType;

  public static Operation create(
      CreateOperationDto dto, final Investor investor, final Stock stock) {

    return Operation.builder()
        .amount(dto.getAmount())
        .limitedPrize(dto.getLimitedPrize())
        .operationType(dto.getOperationType())
        .orderType(dto.getOrderType())
        .investor(investor)
        .stock(stock)
        .build();
  }
}
