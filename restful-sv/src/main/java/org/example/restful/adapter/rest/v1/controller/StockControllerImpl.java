package org.example.restful.adapter.rest.v1.controller;

import org.example.restful.adapter.rest.HateoasUtils;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.fge.jsonpatch.JsonPatch;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import static org.example.restful.constant.Roles.ADMIN;
import static org.example.restful.constant.Roles.USER;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;

@RestController
@RequestMapping(StockControllerImpl.PATH)
@Slf4j
public class StockControllerImpl extends HateoasUtils<StockResponse> implements StockController {

  public static final String PATH = "/api/v1/invest";
  public static final String SLASH = "/";
  public static final String SUBPATH = SLASH + "stock";
  private static final String ID_PATH_PARAM = SLASH + "{isin}";

  @Autowired private StockService stockService;

  @Autowired private StockToStockResponseConverter responseConverter;
  @Autowired private StockRequestToStockConverter requestConverter;

  @Override
  @RolesAllowed({USER, ADMIN})
  @Cacheable(value = "allstocks")
  @GetMapping(value = SUBPATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<StockResponse>> getAllStocks() {
    log.info("Getting all stocks");

    final List<StockResponse> responses = responseConverter.convert(stockService.getAllStocks());

    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(cacheTTL.getAllStocksTTL(), TimeUnit.MILLISECONDS))
        .lastModified(Instant.now())
        .body(responses);
  }

  @Override
  @RolesAllowed(ADMIN)
  @PostMapping(
      value = SUBPATH,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StockResponse> createStock(
      @Valid @RequestBody final StockRequest stockRequest) {
    log.info("Creating stock {}", stockRequest.getIsin());

    final Stock stock = stockService.createStock(requestConverter.convert(stockRequest));

    final StockResponse response = responseConverter.convert(stock);

    applyHATEOAS(response, getHateoasMap(response, List.of(GET, PATCH)));

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Override
  @RolesAllowed(ADMIN)
  @PatchMapping(
      value = SUBPATH + ID_PATH_PARAM,
      consumes = "application/json-patch+json",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StockResponse> modifyStock(
      @PathVariable final String isin, @RequestBody final JsonPatch patch) {
    log.info("Modifying stock {}", isin);

    final StockResponse response = responseConverter.convert(stockService.updateStock(isin, patch));

    applyHATEOAS(response, getHateoasMap(response, List.of(GET, PATCH)));

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  protected Map<RequestMethod, WebMvcLinkBuilder> getHateoasMap(
      final StockResponse response, List<RequestMethod> includedLinks) {

    Map<RequestMethod, WebMvcLinkBuilder> hateoasMap =
        new HashMap<RequestMethod, WebMvcLinkBuilder>();
    try {
      if (includedLinks.contains(RequestMethod.GET)) {
        hateoasMap.put(GET, linkTo(methodOn(this.getClass()).getAllStocks()));
      }
      if (includedLinks.contains(RequestMethod.PATCH)) {
        hateoasMap.put(
            PATCH, linkTo(methodOn(this.getClass()).modifyStock(response.getIsin(), null)));
      }
    } catch (Exception ex) {
      log.error("Error during hateoasMap construction: {}", ex.getMessage());
    }
    return hateoasMap;
  }
}
