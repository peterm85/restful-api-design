package org.example.restful.port.rest.v1;

import java.util.List;

import org.example.restful.port.rest.v1.api.model.StockRequest;
import org.example.restful.port.rest.v1.api.model.StockResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.github.fge.jsonpatch.JsonPatch;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition
@Tag(name = "Stock API", description = "Endpoints for stock operations")
public interface StockController {

  @Operation(summary = "Returns all stocks")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Success",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = StockResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(
            responseCode = "500",
            description = "Server Error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  ResponseEntity<List<StockResponse>> getAllStocks();

  @Operation(summary = "Create a new stock")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
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
  ResponseEntity<StockResponse> createStock(StockRequest stockRequest) throws Exception;

  @Operation(summary = "Modify a stock")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Changes applied",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(
            responseCode = "404",
            description = "Stock not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(
            responseCode = "500",
            description = "Server Error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      })
  ResponseEntity<StockResponse> modifyStock(
      @Schema(
              description = "International Securities Identification Number",
              example = "ES0105611000")
          String isin,
      @Schema(
              description = "JSON Patch",
              example = "[{\"op\":\"replace\",\"path\":\"/currency\",\"value\":\"USD\"}]")
          JsonPatch patch);
}
