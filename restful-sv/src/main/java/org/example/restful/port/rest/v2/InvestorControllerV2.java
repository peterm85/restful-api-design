package org.example.restful.port.rest.v2;

import org.example.restful.port.rest.v1.api.model.InvestorResponse;
import org.example.restful.port.rest.v2.api.model.InvestorResponsePage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition
@Tag(name = "Investor API", description = "Endpoints for investor operations")
public interface InvestorControllerV2 {
  @Operation(summary = "Returns all investors by page")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Success",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = InvestorResponse.class))),
        @ApiResponse(
            responseCode = "206",
            description = "Partial content",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = InvestorResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(
            responseCode = "500",
            description = "Server Error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  ResponseEntity<InvestorResponsePage> getAllInvestors(
      @Parameter(description = "Pageable offset", example = "0") final Optional<Long> offset,
      @Parameter(description = "Pageable limit", example = "50") final Optional<Integer> limit);
}
