package org.example.restful.port.rest.v1.api.model;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(Include.NON_EMPTY)
public class StockResponse extends RepresentationModel<StockResponse> {
  @Schema(description = "International Securities Identification Number", example = "ES0105611000")
  private String isin;

  @Schema(description = "Corporation name", example = "Singular People")
  private String corporationName;

  @Schema(description = "Market", example = "MAD")
  private String market;

  @Schema(description = "Currency", example = "EUR")
  private String currency;
}
