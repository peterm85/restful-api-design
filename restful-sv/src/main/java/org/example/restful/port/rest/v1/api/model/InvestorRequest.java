package org.example.restful.port.rest.v1.api.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvestorRequest {
  @Schema(description = "Identification number", example = "76245691H")
  @NotNull
  @NotBlank
  private String idNumber;

  @Schema(description = "Investor name", example = "Manuel Rodriguez")
  @NotNull
  @NotBlank
  private String name;

  @Schema(description = "Investor age", example = "37")
  @NotNull
  @Min(18)
  @Max(99)
  private Integer age;

  @Schema(description = "Investor country", example = "SPAIN")
  @NotNull
  @NotBlank
  private String country;
}
