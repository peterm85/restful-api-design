package org.example.restful.port.rest.v1.api.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockRequest {
  @Schema(description = "International Securities Identification Number", example = "ES0105611000")
  @NotNull
  @NotBlank
  private String isin;

  @Schema(description = "Corporation name", example = "Singular People")
  @NotNull
  @NotBlank
  private String corporationName;

  @Schema(description = "Market", example = "MAD")
  @NotNull
  @NotBlank
  private String market;

  @Schema(description = "Currency", example = "EUR")
  @NotNull
  @NotBlank
  private String currency;
}
