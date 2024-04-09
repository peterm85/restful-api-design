package org.example.restful.port.rest.v1.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseResponse {
  @Schema(description = "Operation id", example = "1")
  private Long id;

  @Schema(description = "International Securities Identification Number", example = "ES0105611000")
  private String isin;

  @Schema(description = "Identification number", example = "76245691H")
  private String idNumber;

  @Schema(description = "Amount of shares to buy", example = "150")
  private Integer amount;

  @Schema(description = "Maximum prize to buy a share", example = "3,456")
  private Double limitedPrize;

  @Schema(description = "Type of order", example = "DAILY or PERMANENT")
  private OrderTypeRequest orderType;
}
