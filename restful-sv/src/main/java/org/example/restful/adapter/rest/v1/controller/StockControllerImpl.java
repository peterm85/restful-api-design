package org.example.restful.adapter.rest.v1.controller;

import java.util.List;

import org.example.restful.adapter.rest.v1.converter.StockRequestToStockConverter;
import org.example.restful.adapter.rest.v1.converter.StockToStockResponseConverter;
import org.example.restful.domain.Stock;
import org.example.restful.port.rest.v1.StockController;
import org.example.restful.port.rest.v1.api.model.StockRequest;
import org.example.restful.port.rest.v1.api.model.StockResponse;
import org.example.restful.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(StockControllerImpl.PATH)
@Slf4j
public class StockControllerImpl implements StockController {

  public static final String PATH = "/api/v1/invest";
  public static final String SLASH = "/";
  public static final String SUBPATH = SLASH + "stock";

  @Autowired private StockService stockService;

  @Autowired private StockToStockResponseConverter responseConverter;
  @Autowired private StockRequestToStockConverter requestConverter;

  @Override
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  @GetMapping(value = SUBPATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<StockResponse>> getAllStocks() {
    log.info("Getting all stocks");
    return ResponseEntity.ok().body(responseConverter.convert(stockService.getAllStocks()));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(
      value = SUBPATH,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StockResponse> createStock(@RequestBody final StockRequest investorRequest)
      throws Exception {

    final Stock stock = stockService.createStock(requestConverter.convert(investorRequest));

    final StockResponse response = responseConverter.convert(stock);

    return ResponseEntity.status(HttpStatus.CREATED).body(applyHATEOAS(response));
  }

  private StockResponse applyHATEOAS(StockResponse response) {

    response.add(
        linkTo(methodOn(StockControllerImpl.class).getAllStocks())
            .withRel(RequestMethod.GET.name()));

    return response;
  }
}
