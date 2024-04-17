package org.example.restful.adapter.rest.v1.controller;

import org.example.restful.adapter.rest.HateoasUtils;
import org.example.restful.adapter.rest.v1.converter.InvestorRequestToInvestorConverter;
import org.example.restful.adapter.rest.v1.converter.InvestorToInvestorResponseConverter;
import org.example.restful.domain.Investor;
import org.example.restful.port.rest.v1.InvestorController;
import org.example.restful.port.rest.v1.api.model.InvestorRequest;
import org.example.restful.port.rest.v1.api.model.InvestorResponse;
import org.example.restful.service.InvestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import static org.example.restful.constant.Roles.ADMIN;
import static org.example.restful.constant.Roles.USER;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Slf4j
@RestController
@RequestMapping(InvestorControllerImpl.PATH)
public class InvestorControllerImpl extends HateoasUtils<InvestorResponse>
    implements InvestorController {

  public static final String PATH = "/api/v1/invest";
  public static final String NEW_PATH = "/api/v2/invest";
  public static final String SLASH = "/";
  public static final String SUBPATH = SLASH + "investor";
  private static final String ID_PATH_PARAM = SLASH + "{id}";

  @Autowired private InvestorService investorService;

  @Autowired private InvestorToInvestorResponseConverter responseConverter;
  @Autowired private InvestorRequestToInvestorConverter requestConverter;

  @Override
  @RolesAllowed({USER, ADMIN})
  @Cacheable(value = "investor")
  @GetMapping(
      value = SUBPATH + ID_PATH_PARAM,
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<InvestorResponse> getInvestor(@PathVariable final Long id) {
    log.info("Getting investor {}", id);

    final InvestorResponse response =
        responseConverter.convert(investorService.getInvestorById(id));

    applyHATEOAS(response, getHateoasMap(response, List.of(PUT, DELETE)));

    return ResponseEntity.ok().body(response);
  }

  @Override
  @Deprecated
  @RolesAllowed(ADMIN)
  @GetMapping(value = SUBPATH)
  public ResponseEntity<List<InvestorResponse>> getAllInvestors() {
    log.info("Getting all investors");
    return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
        .location(
            UriComponentsBuilder.newInstance()
                .path(NEW_PATH.concat(SUBPATH))
                .query("offset=" + PAGINATION_DEFAULT_OFFSET + "&limit=" + PAGINATION_DEFAULT_LIMIT)
                .build()
                .toUri())
        .build();
  }

  @Override
  @RolesAllowed({USER, ADMIN})
  @PostMapping(
      value = SUBPATH,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InvestorResponse> createInvestor(
      @Valid @RequestBody final InvestorRequest investorRequest) throws Exception {
    log.info("Creating investor {}", investorRequest.getIdNumber());

    final Investor investor =
        investorService.createInvestor(requestConverter.convert(investorRequest));

    final InvestorResponse response = responseConverter.convert(investor);

    applyHATEOAS(response, getHateoasMap(response, List.of(GET, PUT, DELETE)));

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @SuppressWarnings("rawtypes")
  @Override
  @RolesAllowed({USER, ADMIN})
  @CacheEvict(value = "investor", key = "#id")
  @PutMapping(value = SUBPATH + ID_PATH_PARAM, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity updateInvestor(
      @PathVariable final Long id, @RequestBody final InvestorRequest investorRequest) {
    log.info("Updating investor {}", id);

    investorService.updateInvestor(id, requestConverter.convert(investorRequest));

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @SuppressWarnings("rawtypes")
  @Override
  @RolesAllowed({USER, ADMIN})
  @CacheEvict(value = "investor", key = "#id")
  @DeleteMapping(value = SUBPATH + ID_PATH_PARAM, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity deleteInvestor(@PathVariable final Long id) {
    log.info("Deleting investor {}", id);

    investorService.deleteInvestor(id);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  protected Map<RequestMethod, WebMvcLinkBuilder> getHateoasMap(
      InvestorResponse response, List<RequestMethod> includedLinks) {

    Map<RequestMethod, WebMvcLinkBuilder> hateoasMap =
        new HashMap<RequestMethod, WebMvcLinkBuilder>();

    if (includedLinks.contains(RequestMethod.GET)) {
      hateoasMap.put(GET, linkTo(methodOn(this.getClass()).getInvestor(response.getId())));
    }
    if (includedLinks.contains(RequestMethod.PUT)) {
      hateoasMap.put(
          PUT,
          linkTo(
              methodOn(this.getClass())
                  .updateInvestor(response.getId(), InvestorRequest.builder().build())));
    }
    if (includedLinks.contains(RequestMethod.DELETE)) {
      hateoasMap.put(DELETE, linkTo(methodOn(this.getClass()).deleteInvestor(response.getId())));
    }

    return hateoasMap;
  }
}
