package org.example.restful.adapter.rest.v1.controller;

import org.example.restful.adapter.rest.HateoasUtils;
import org.example.restful.adapter.rest.v1.converter.JsonPatchRequestToJsonPatchConverter;
import org.example.restful.adapter.rest.v1.converter.StockRequestToStockConverter;
import org.example.restful.adapter.rest.v1.converter.StockToStockResponseConverter;
import org.example.restful.domain.Stock;
import org.example.restful.service.StockService;
import org.openapitools.api.StocksApi;
import org.openapitools.model.JsonPatchRequest;
import org.openapitools.model.StockRequest;
import org.openapitools.model.StockResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.fge.jsonpatch.JsonPatch;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.security.RolesAllowed;

import lombok.extern.slf4j.Slf4j;

import static org.example.restful.constant.Roles.ADMIN;
import static org.example.restful.constant.Roles.USER;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;

@RestController
@Slf4j
public class StockControllerImpl extends HateoasUtils implements StocksApi {

  @Autowired private StockService stockService;

  @Autowired private StockToStockResponseConverter responseConverter;
  @Autowired private StockRequestToStockConverter requestConverter;
  @Autowired private JsonPatchRequestToJsonPatchConverter patchAdapter;

  @Override
  @RolesAllowed({USER, ADMIN})
  @Cacheable(value = "allstocks")
  public ResponseEntity<List<StockResponse>> getStocks() {
    log.info("Getting all stocks");

    final List<StockResponse> responses = responseConverter.convert(stockService.getAllStocks());

    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(cacheTTL.getAllStocksTTL(), TimeUnit.MILLISECONDS))
        .lastModified(Instant.now())
        .body(responses);
  }

  @Override
  @RolesAllowed(ADMIN)
  public ResponseEntity<StockResponse> createStock(final StockRequest stockRequest) {
    log.info("Creating stock {}", stockRequest.getIsin());

    final Stock stock = stockService.createStock(requestConverter.convert(stockRequest));

    final StockResponse response = responseConverter.convert(stock);
    response.setLinks(responseConverter.convertLinks(getHateoasMap(response, List.of(GET, PATCH))));

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Override
  @RolesAllowed(ADMIN)
  public ResponseEntity<StockResponse> updateStock(
      final String isin, final JsonPatchRequest patchRequest) {
    log.info("Modifying stock {}", isin);

    final JsonPatch patch = this.patchAdapter.convert(patchRequest);

    final StockResponse response = responseConverter.convert(stockService.updateStock(isin, patch));
    response.setLinks(responseConverter.convertLinks(getHateoasMap(response, List.of(GET, PATCH))));

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  protected Map<RequestMethod, WebMvcLinkBuilder> getHateoasMap(
      final StockResponse response, List<RequestMethod> includedLinks) {

    Map<RequestMethod, WebMvcLinkBuilder> hateoasMap =
        new HashMap<RequestMethod, WebMvcLinkBuilder>();
    try {
      if (includedLinks.contains(RequestMethod.GET)) {
        hateoasMap.put(GET, linkTo(methodOn(this.getClass()).getStocks()));
      }
      if (includedLinks.contains(RequestMethod.PATCH)) {
        hateoasMap.put(
            PATCH, linkTo(methodOn(this.getClass()).updateStock(response.getIsin(), null)));
      }
    } catch (Exception ex) {
      log.error("Error during hateoasMap construction: {}", ex.getMessage());
    }
    return hateoasMap;
  }
}
