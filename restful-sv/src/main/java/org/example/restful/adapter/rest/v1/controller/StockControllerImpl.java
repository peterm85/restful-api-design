package org.example.restful.adapter.rest.v1.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.security.RolesAllowed;

import org.example.restful.adapter.rest.RestfulAPIController;
import org.example.restful.adapter.rest.v1.converter.StockRequestToStockConverter;
import org.example.restful.adapter.rest.v1.converter.StockToStockResponseConverter;
import org.example.restful.domain.Stock;
import org.example.restful.port.rest.v1.StockController;
import org.example.restful.port.rest.v1.api.model.StockRequest;
import org.example.restful.port.rest.v1.api.model.StockResponse;
import org.example.restful.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import static org.example.restful.constant.Roles.ADMIN;
import static org.example.restful.constant.Roles.USER;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(StockControllerImpl.PATH)
@Slf4j
public class StockControllerImpl extends RestfulAPIController<StockResponse>
    implements StockController {

  public static final String PATH = "/api/v1/invest";
  public static final String SLASH = "/";
  public static final String SUBPATH = SLASH + "stock";

  @Autowired private StockService stockService;

  @Autowired private StockToStockResponseConverter responseConverter;
  @Autowired private StockRequestToStockConverter requestConverter;

  @Override
  @RolesAllowed({USER, ADMIN})
  @Cacheable(value = "allstocks")
  @GetMapping(value = SUBPATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<StockResponse>> getAllStocks() {
    log.info("Getting all stocks");

    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(cacheTTL.getAllStocksTTL(), TimeUnit.MILLISECONDS))
        .header("X-Last-Modified", dateFormat.format(new Date()))
        .body(responseConverter.convert(stockService.getAllStocks()));
  }

  @Override
  @RolesAllowed(ADMIN)
  @PostMapping(
      value = SUBPATH,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StockResponse> createStock(
      @RequestBody final StockRequest investorRequest) {

    final Stock stock = stockService.createStock(requestConverter.convert(investorRequest));

    final StockResponse response = responseConverter.convert(stock);

    applyHATEOAS(response, List.of(GET));

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Override
  protected Map<RequestMethod, WebMvcLinkBuilder> hateoasMap(StockResponse response) {
    Map<RequestMethod, WebMvcLinkBuilder> hateoasMap =
        new HashMap<RequestMethod, WebMvcLinkBuilder>();

    hateoasMap.put(GET, linkTo(methodOn(this.getClass()).getAllStocks()));

    return hateoasMap;
  }
}
