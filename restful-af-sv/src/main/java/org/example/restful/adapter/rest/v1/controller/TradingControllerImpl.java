package org.example.restful.adapter.rest.v1.controller;

import org.example.restful.adapter.rest.v1.converter.OperationToPurchaseResponseConverter;
import org.example.restful.adapter.rest.v1.converter.PurchaseBatchRequestToCreateOperationDtoConverter;
import org.example.restful.adapter.rest.v1.converter.PurchaseRequestToCreateOperationDtoConverter;
import org.example.restful.domain.Operation;
import org.example.restful.service.TradingService;
import org.openapitools.api.TradingApi;
import org.openapitools.model.PurchaseBatchRequest;
import org.openapitools.model.PurchaseRequest;
import org.openapitools.model.PurchaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import lombok.extern.slf4j.Slf4j;

import static java.util.concurrent.CompletableFuture.runAsync;
import static org.example.restful.constant.Roles.ADMIN;
import static org.example.restful.constant.Roles.USER;

@Slf4j
@RestController
public class TradingControllerImpl implements TradingApi {

  @Autowired private TradingService tradingService;

  @Autowired private PurchaseRequestToCreateOperationDtoConverter requestConverter;
  @Autowired private PurchaseBatchRequestToCreateOperationDtoConverter requestBatchConverter;
  @Autowired private OperationToPurchaseResponseConverter responseConverter;

  @Override
  @RolesAllowed(USER)
  public ResponseEntity<PurchaseResponse> purchase(
      final Long investorId, final PurchaseRequest purchaseRequest) {
    log.info("Purchasing shares by investor {}", investorId);

    final Operation operation =
        tradingService.purchase(
            requestConverter.convert(purchaseRequest), investorId, purchaseRequest.getIsin());

    return ResponseEntity.status(HttpStatus.CREATED).body(responseConverter.convert(operation));
  }

  @Override
  @RolesAllowed(ADMIN)
  public ResponseEntity<Void> batchPurchase(
      final List<PurchaseBatchRequest> purchaseBatchRequestList) {
    log.info("Starting batch purchase operation");

    runAsync(
        () ->
            purchaseBatchRequestList.stream()
                .forEach(
                    request ->
                        tradingService.purchase(
                            requestBatchConverter.convert(request),
                            request.getInvestorId(),
                            request.getIsin())));

    log.info("Ending batch purchase operation");
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }
}
