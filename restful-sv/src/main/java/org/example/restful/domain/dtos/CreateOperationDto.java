package org.example.restful.domain.dtos;

import org.example.restful.domain.OperationType;
import org.example.restful.domain.OrderType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateOperationDto {

  private Integer amount;
  private Double limitedPrize;
  private OrderType orderType;
  private OperationType operationType;
}
