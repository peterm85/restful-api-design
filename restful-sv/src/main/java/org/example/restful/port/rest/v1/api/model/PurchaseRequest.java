package org.example.restful.port.rest.v1.api.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseRequest {
  @Schema(description = "International Securities Identification Number", example = "ES0105611000")
  @NotNull
  @NotBlank
  private String isin;

  @Schema(description = "Amount of shares to buy", example = "150")
  @NotNull
  @Min(1)
  private Integer amount;

  @Schema(description = "Maximum prize to buy a share", example = "3,456")
  @NotNull
  private Double limitedPrize;

  @Schema(description = "Type of order", example = "DAILY or PERMANENT")
  @NotNull
  private OrderTypeRequest orderType;
}
