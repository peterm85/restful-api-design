package org.example.restful.port.rest.v1;

import org.example.restful.port.rest.v1.api.model.PurchaseBatchRequest;
import org.example.restful.port.rest.v1.api.model.PurchaseRequest;
import org.example.restful.port.rest.v1.api.model.PurchaseResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition
@Tag(name = "Trading API", description = "Endpoints for trading operations")
public interface TradingController {

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
  ResponseEntity<PurchaseResponse> purchase(
      @Parameter(description = "Identification", example = "12345") Long id,
      PurchaseRequest purchaseRequest);

  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "202",
            description = "Accepted",
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
  ResponseEntity<Void> batchPurchase(List<PurchaseBatchRequest> purchaseRequests);
}
