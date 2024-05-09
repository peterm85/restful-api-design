package org.example.restful.adapter.rest.v1.controller;

import org.example.restful.adapter.rest.v1.converter.OperationToPurchaseResponseConverter;
import org.example.restful.adapter.rest.v1.converter.PurchaseBatchRequestToCreateOperationDtoConverter;
import org.example.restful.adapter.rest.v1.converter.PurchaseRequestToCreateOperationDtoConverter;
import org.example.restful.domain.Operation;
import org.example.restful.port.rest.v1.TradingController;
import org.example.restful.port.rest.v1.api.model.PurchaseBatchRequest;
import org.example.restful.port.rest.v1.api.model.PurchaseRequest;
import org.example.restful.port.rest.v1.api.model.PurchaseResponse;
import org.example.restful.service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import static java.util.concurrent.CompletableFuture.runAsync;
import static org.example.restful.constant.Roles.ADMIN;
import static org.example.restful.constant.Roles.USER;
import static org.example.restful.constant.UrlConstants.BASE_PATH_V1;
import static org.example.restful.constant.UrlConstants.ID_PATH_PARAM;
import static org.example.restful.constant.UrlConstants.INVESTORS_SUBPATH;
import static org.example.restful.constant.UrlConstants.PURCHASE_ACTION;

@Slf4j
@RestController
@RequestMapping(BASE_PATH_V1)
public class TradingControllerImpl implements TradingController {

  @Autowired private TradingService tradingService;

  @Autowired private PurchaseRequestToCreateOperationDtoConverter requestConverter;
  @Autowired private PurchaseBatchRequestToCreateOperationDtoConverter requestBatchConverter;
  @Autowired private OperationToPurchaseResponseConverter responseConverter;

  @Override
  @RolesAllowed(USER)
  @PostMapping(
      value = INVESTORS_SUBPATH + ID_PATH_PARAM + PURCHASE_ACTION,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PurchaseResponse> purchase(
      @PathVariable final Long id, @Valid @RequestBody final PurchaseRequest purchaseRequest) {
    log.info("Purchasing shares from investor {}", id);

    final Operation operation =
        tradingService.purchase(
            requestConverter.convert(purchaseRequest), id, purchaseRequest.getIsin());

    return ResponseEntity.status(HttpStatus.CREATED).body(responseConverter.convert(operation));
  }

  @Override
  @RolesAllowed(ADMIN)
  @PatchMapping(
      value = INVESTORS_SUBPATH + PURCHASE_ACTION,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> batchPurchase(
      @Valid @RequestBody final List<PurchaseBatchRequest> purchaseRequests) {
    log.info("Starting batch purchase operation");

    runAsync(
        () ->
            purchaseRequests.stream()
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
