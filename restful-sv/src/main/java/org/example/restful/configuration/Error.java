package org.example.restful.configuration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Error {
  @Schema(description = "Error code", example = "ERRXXX")
  private String code;

  @Schema(description = "Error message", example = "Error message")
  private String message;
}
