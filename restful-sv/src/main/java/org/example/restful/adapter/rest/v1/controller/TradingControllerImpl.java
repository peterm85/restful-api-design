package org.example.restful.adapter.rest.v1.controller;

import org.example.restful.adapter.rest.v1.converter.OperationToPurchaseResponseConverter;
import org.example.restful.adapter.rest.v1.converter.PurchaseBatchRequestToOperationConverter;
import org.example.restful.adapter.rest.v1.converter.PurchaseRequestToOperationConverter;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.example.restful.constant.Roles.ADMIN;
import static org.example.restful.constant.Roles.USER;

@RestController
@RequestMapping(TradingControllerImpl.PATH)
public class TradingControllerImpl implements TradingController {

  public static final String PATH = "/api/v1/invest";
  public static final String SLASH = "/";
  public static final String SUBPATH = SLASH + "investor";
  private static final String ID_PATH_PARAM = SLASH + "{id}";
  public static final String PURCHASE_OPERATION = SLASH + "purchase";
  private static final String PURCHASE_OPERATION_PATH =
      SUBPATH + ID_PATH_PARAM + PURCHASE_OPERATION;
  private static final String PURCHASE_BATCH_OPERATION_PATH = SUBPATH + PURCHASE_OPERATION;

  @Autowired private TradingService tradingService;

  @Autowired private PurchaseRequestToOperationConverter requestConverter;
  @Autowired private PurchaseBatchRequestToOperationConverter requestBatchConverter;
  @Autowired private OperationToPurchaseResponseConverter responseConverter;

  @Override
  @RolesAllowed(USER)
  @PostMapping(value = PURCHASE_OPERATION_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PurchaseResponse> purchase(
      @PathVariable final Long id, @Valid @RequestBody final PurchaseRequest purchaseRequest) {

    final Operation operation =
        tradingService.purchase(id, requestConverter.convert(purchaseRequest));

    return ResponseEntity.status(HttpStatus.CREATED).body(responseConverter.convert(operation));
  }

  @Override
  @RolesAllowed(ADMIN)
  @PostMapping(value = PURCHASE_BATCH_OPERATION_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> batchPurchase(
      @Valid @RequestBody final List<PurchaseBatchRequest> purchaseRequests) {

    supplyAsync(
        () ->
            purchaseRequests.stream()
                .map(
                    request ->
                        tradingService.purchase(
                            request.getInvestorId(), requestBatchConverter.convert(request))));

    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }
}
