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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;

import static org.example.restful.constant.Roles.ADMIN;

@SuppressWarnings("rawtypes")
@RestController
@RequestMapping(InvestorControllerV2Impl.PATH)
public class InvestorControllerV2Impl extends HateoasUtils implements InvestorControllerV2 {

  public static final String PATH = "/api/v2/invest";
  public static final String SLASH = "/";
  public static final String SUBPATH = SLASH + "investor";

  @Autowired private InvestorService investorService;

  @Autowired private InvestorToInvestorResponseConverter converter;

  @SuppressWarnings("unchecked")
  @Override
  @RolesAllowed(ADMIN)
  @GetMapping(value = SUBPATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InvestorResponsePage> getAllInvestors(
      final Optional<Long> offset, final Optional<Integer> limit) {

    final Pageable pageable = getPageable(offset, limit);

    final Page<Investor> resultPage = investorService.getAllInvestors(pageable);

    return ResponseEntity.ok(
        InvestorResponsePage.builder()
            .data(resultPage.map(converter::convert).stream().collect(Collectors.toList()))
            .pagination(
                getPagination(
                    offset.orElse(PAGINATION_DEFAULT_OFFSET),
                    limit.orElse(PAGINATION_DEFAULT_LIMIT),
                    resultPage.getTotalElements()))
            .build());
  }
}
