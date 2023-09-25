package org.example.restful.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Operation {
  private Long id;
  private String isin;
  private String idNumber;
  private Integer amount;
  private Double limitedPrize;
  private OrderType orderType;
  private OperationType operationType;
}
