package org.example.restful.adapter.rest.v2.controller;

import org.example.restful.adapter.rest.HateoasUtils;
import org.example.restful.adapter.rest.v1.converter.InvestorToInvestorResponseConverter;
import org.example.restful.domain.Investor;
import org.example.restful.port.rest.v2.InvestorControllerV2;
import org.example.restful.port.rest.v2.api.model.InvestorResponsePage;
import org.example.restful.service.InvestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;

import static org.example.restful.constant.Roles.ADMIN;
import static org.example.restful.constant.UrlConstants.BASE_PATH_V2;
import static org.example.restful.constant.UrlConstants.INVESTORS_SUBPATH;

@SuppressWarnings("rawtypes")
@RestController
@RequestMapping(BASE_PATH_V2)
public class InvestorControllerV2Impl extends HateoasUtils implements InvestorControllerV2 {

  @Autowired private InvestorService investorService;

  @Autowired private InvestorToInvestorResponseConverter converter;

  @SuppressWarnings("unchecked")
  @Override
  @RolesAllowed(ADMIN)
  @GetMapping(value = INVESTORS_SUBPATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InvestorResponsePage> getAllInvestors(
      final Optional<Long> offset, final Optional<Integer> limit) {

    final Pageable pageable = getPageable(offset, limit);

    final Page<Investor> resultPage = investorService.getAllInvestors(pageable);

    return ResponseEntity.status(calculateStatus(resultPage))
        .body(
            InvestorResponsePage.builder()
                .data(resultPage.map(converter::convert).stream().collect(Collectors.toList()))
                .pagination(
                    getPagination(
                        offset.orElse(PAGINATION_DEFAULT_OFFSET),
                        limit.orElse(PAGINATION_DEFAULT_LIMIT),
                        resultPage.getTotalElements()))
                .build());
  }

  private HttpStatus calculateStatus(final Page<Investor> resultPage) {
    return resultPage.getTotalElements() > resultPage.getSize()
        ? HttpStatus.PARTIAL_CONTENT
        : HttpStatus.OK;
  }
}
