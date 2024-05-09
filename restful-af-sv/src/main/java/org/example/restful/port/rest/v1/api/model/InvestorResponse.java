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
public class InvestorResponse extends RepresentationModel<InvestorResponse> {

  @Schema(description = "Identification", example = "12345")
  private Long id;

  @Schema(description = "Identification number", example = "76245691H")
  private String idNumber;

  @Schema(description = "Investor name", example = "Manuel Rodriguez")
  private String name;

  @Schema(description = "Investor age", example = "37")
  private Integer age;

  @Schema(description = "Investor country", example = "SPAIN")
  private String country;
}
