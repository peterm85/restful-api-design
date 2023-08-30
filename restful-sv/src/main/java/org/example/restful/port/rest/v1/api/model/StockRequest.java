package org.example.restful.port.rest.v1.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockRequest {
  @Schema(description = "International Securities Identification Number", example = "ES0105611000")
  private String isin;

  @Schema(description = "Corporation name", example = "Singular People")
  private String corporationName;

  @Schema(description = "Market", example = "MAD")
  private String market;

  @Schema(description = "Currency", example = "EUR")
  private String currency;
}
