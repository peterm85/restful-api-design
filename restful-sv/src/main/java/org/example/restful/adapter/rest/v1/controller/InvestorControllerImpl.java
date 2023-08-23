package org.example.restful.adapter.rest.v1.controller;

import java.util.Collections;
import java.util.List;

import org.example.restful.adapter.rest.v1.converter.InvestorToInvestorResponseConverter;
import org.example.restful.port.rest.v1.InvestorController;
import org.example.restful.port.rest.v1.api.model.InvestorResponse;
import org.example.restful.service.InvestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(InvestorControllerImpl.PATH)
public class InvestorControllerImpl implements InvestorController {

  public static final String PATH = "/api/v1/invest";
  public static final String NEW_PATH = "/api/v2/invest";
  public static final String SLASH = "/";
  public static final String SUBPATH = SLASH + "investor";
  private static final String ID_PATH_PARAM = SLASH + "{idNumber}";

  @Autowired private InvestorService investorService;

  @Autowired private InvestorToInvestorResponseConverter converter;

  @Override
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  @GetMapping(value = SUBPATH + ID_PATH_PARAM, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InvestorResponse> getInvestor(@PathVariable final String idNumber) {

    final InvestorResponse response =
        converter.convert(investorService.getInvestorByIdNumber(idNumber));

    return ResponseEntity.ok(response);
  }

  @SuppressWarnings("unchecked")
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping(value = SUBPATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<InvestorResponse>> getAllInvestors() {

    return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
        .location(
            UriComponentsBuilder.newInstance()
                .path(NEW_PATH.concat(SUBPATH))
                .query("page=0&size=3")
                .build()
                .toUri())
        .body(Collections.EMPTY_LIST);
  }
}
