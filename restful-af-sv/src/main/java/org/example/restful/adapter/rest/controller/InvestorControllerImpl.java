package org.example.restful.adapter.rest.controller;

import org.example.restful.adapter.rest.HateoasUtils;
import org.example.restful.adapter.rest.converter.InvestorRequestToInvestorConverter;
import org.example.restful.adapter.rest.converter.InvestorToInvestorResponseConverter;
import org.example.restful.configuration.CacheTTL;
import org.example.restful.domain.Investor;
import org.example.restful.service.InvestorService;
import org.openapitools.api.InvestorsApi;
import org.openapitools.model.InvestorRequest;
import org.openapitools.model.InvestorResponse;
import org.openapitools.model.InvestorResponsePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;

import lombok.extern.slf4j.Slf4j;

import static org.example.restful.constant.Roles.ADMIN;
import static org.example.restful.constant.Roles.USER;
import static org.example.restful.constant.UrlConstants.BASE_PATH_V2;
import static org.example.restful.constant.UrlConstants.INVESTORS_SUBPATH;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Slf4j
@RestController
public class InvestorControllerImpl extends HateoasUtils implements InvestorsApi {

  @Autowired protected CacheTTL cacheTTL;

  @Autowired private InvestorService investorService;

  @Autowired private InvestorToInvestorResponseConverter responseConverter;
  @Autowired private InvestorRequestToInvestorConverter requestConverter;

  @Override
  @RolesAllowed({USER, ADMIN})
  @Cacheable(value = "investor")
  public ResponseEntity<InvestorResponse> getInvestorById(final Long id) {
    log.info("Getting investor {}", id);

    final InvestorResponse response =
        responseConverter.convert(investorService.getInvestorById(id));
    response.setLinks(
        responseConverter.convertLinks(getHateoasMap(response, List.of(PUT, DELETE))));

    return ResponseEntity.ok().lastModified(Instant.now()).body(response);
  }

  @Override
  @Deprecated
  @RolesAllowed(ADMIN)
  public ResponseEntity<Void> getInvestors() {
    log.info("Getting all investors");
    return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
        .location(
            UriComponentsBuilder.newInstance()
                .path(BASE_PATH_V2.concat(INVESTORS_SUBPATH))
                .query("offset=" + PAGINATION_DEFAULT_OFFSET + "&limit=" + PAGINATION_DEFAULT_LIMIT)
                .build()
                .toUri())
        .build();
  }

  @Override
  @SuppressWarnings("unchecked")
  @RolesAllowed(ADMIN)
  public ResponseEntity<InvestorResponsePage> getInvestorsV2(
      final Long offset, final Integer limit) {

    final Optional<Long> optOffset = Optional.ofNullable(offset);
    final Optional<Integer> optLimit = Optional.ofNullable(limit);

    final Pageable pageable = getPageable(optOffset, optLimit);

    final Page<Investor> resultPage = investorService.getAllInvestors(pageable);

    return ResponseEntity.status(calculateStatus(resultPage))
        .body(
            InvestorResponsePage.builder()
                .data(
                    resultPage.map(responseConverter::convert).stream()
                        .collect(Collectors.toList()))
                .pagination(
                    getPagination(
                        optOffset.orElse(PAGINATION_DEFAULT_OFFSET),
                        optLimit.orElse(PAGINATION_DEFAULT_LIMIT),
                        resultPage.getTotalElements()))
                .build());
  }

  private HttpStatus calculateStatus(final Page<Investor> resultPage) {
    return resultPage.getTotalElements() > resultPage.getSize()
        ? HttpStatus.PARTIAL_CONTENT
        : HttpStatus.OK;
  }

  @Override
  @RolesAllowed({USER, ADMIN})
  public ResponseEntity<InvestorResponse> createInvestor(final InvestorRequest investorRequest) {
    log.info("Creating investor {}", investorRequest.getIdNumber());

    final Investor investor =
        investorService.createInvestor(requestConverter.convert(investorRequest));

    final InvestorResponse response = responseConverter.convert(investor);
    response.setLinks(
        responseConverter.convertLinks(getHateoasMap(response, List.of(GET, PUT, DELETE))));

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Override
  @RolesAllowed({USER, ADMIN})
  @CacheEvict(value = "investor", key = "#id")
  public ResponseEntity<Void> updateInvestor(final Long id, final InvestorRequest investorRequest) {
    log.info("Updating investor {}", id);

    investorService.updateInvestor(id, requestConverter.convert(investorRequest));

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Override
  @RolesAllowed({USER, ADMIN})
  @CacheEvict(value = "investor", key = "#id")
  public ResponseEntity<Void> deleteInvestor(final Long id) {
    log.info("Deleting investor {}", id);

    investorService.deleteInvestor(id);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  protected Map<RequestMethod, WebMvcLinkBuilder> getHateoasMap(
      InvestorResponse response, List<RequestMethod> includedLinks) {

    Map<RequestMethod, WebMvcLinkBuilder> hateoasMap =
        new HashMap<RequestMethod, WebMvcLinkBuilder>();

    if (includedLinks.contains(RequestMethod.GET)) {
      hateoasMap.put(
          GET, linkTo(methodOn(this.getClass()).getInvestorById(response.getId().longValue())));
    }
    if (includedLinks.contains(RequestMethod.PUT)) {
      hateoasMap.put(
          PUT,
          linkTo(
              methodOn(this.getClass())
                  .updateInvestor(
                      response.getId().longValue(), InvestorRequest.builder().build())));
    }
    if (includedLinks.contains(RequestMethod.DELETE)) {
      hateoasMap.put(
          DELETE, linkTo(methodOn(this.getClass()).deleteInvestor(response.getId().longValue())));
    }

    return hateoasMap;
  }
}
