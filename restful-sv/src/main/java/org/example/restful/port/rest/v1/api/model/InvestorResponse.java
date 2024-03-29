package org.example.restful.port.rest.v1.api.model;

import org.springframework.hateoas.RepresentationModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvestorResponse extends RepresentationModel<InvestorResponse> {
  @Schema(description = "Identification number", example = "76245691H")
  private String idNumber;

  @Schema(description = "Investor name", example = "Manuel Rodriguez")
  private String name;

  @Schema(description = "Investor age", example = "37")
  private Integer age;

  @Schema(description = "Investor country", example = "SPAIN")
  private String country;
}
