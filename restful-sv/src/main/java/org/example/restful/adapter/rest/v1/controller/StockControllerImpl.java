package org.example.restful.adapter.rest.v1.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.example.restful.adapter.rest.v1.converter.StockRequestToStockConverter;
import org.example.restful.adapter.rest.v1.converter.StockToStockResponseConverter;
import org.example.restful.configuration.CacheTTL;
import org.example.restful.domain.Stock;
import org.example.restful.port.rest.v1.StockController;
import org.example.restful.port.rest.v1.api.model.StockRequest;
import org.example.restful.port.rest.v1.api.model.StockResponse;
import org.example.restful.service.StockService;
import org.example.restful.utils.RestfulAPIResponse;
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
public class StockControllerImpl implements StockController {

  public static final String PATH = "/api/v1/invest";
  public static final String SLASH = "/";
  public static final String SUBPATH = SLASH + "stock";
  private static final String ID_PATH_PARAM = SLASH + "{isin}";

  @Autowired protected CacheTTL cacheTTL;

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

    return RestfulAPIResponse.status(HttpStatus.OK)
        .cacheControl(CacheControl.maxAge(cacheTTL.getAllStocksTTL(), TimeUnit.MILLISECONDS))
        .lastModified(Instant.now())
        .hateoas(getHateoasMap, List.of(GET, PATCH))
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

    return RestfulAPIResponse.status(HttpStatus.CREATED)
        .hateoas(getHateoasMap, List.of(GET, PATCH))
        .body(response);
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

    final StockResponse response = responseConverter.convert(stockService.modifyStock(isin, patch));

    return RestfulAPIResponse.status(HttpStatus.OK)
        .hateoas(getHateoasMap, List.of(GET, PATCH))
        .body(response);
  }

  private static Function<StockResponse, Map<RequestMethod, WebMvcLinkBuilder>> getHateoasMap =
      response -> {
        Map<RequestMethod, WebMvcLinkBuilder> hateoasMap =
            new HashMap<RequestMethod, WebMvcLinkBuilder>();
        try {

          hateoasMap.put(GET, linkTo(methodOn(StockControllerImpl.class).getAllStocks()));
          hateoasMap.put(
              PATCH,
              linkTo(methodOn(StockControllerImpl.class).modifyStock(response.getIsin(), null)));
        } catch (Exception ex) {
          log.error("Error during hateoasMap construction: {}", ex.getMessage());
        }
        return hateoasMap;
      };
}
