package org.example.restful.adapter.rest.v2.controller;

import java.util.List;

import javax.validation.Valid;

import org.example.restful.adapter.rest.v1.converter.InvestorToInvestorResponseConverter;
import org.example.restful.port.rest.v1.api.model.InvestorResponse;
import org.example.restful.port.rest.v2.InvestorControllerV2;
import org.example.restful.port.rest.v2.api.model.PageableRequest;
import org.example.restful.service.InvestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(InvestorControllerV2Impl.PATH)
public class InvestorControllerV2Impl implements InvestorControllerV2 {

  public static final String PATH = "/api/v2/invest";
  public static final String SLASH = "/";
  public static final String SUBPATH = SLASH + "investor";

  @Autowired private InvestorService investorService;

  @Autowired private InvestorToInvestorResponseConverter converter;

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping(value = SUBPATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<InvestorResponse>> getAllInvestors(
      @Valid final PageableRequest pageableRequest) {

    final List<InvestorResponse> response =
        converter.convert(
            investorService.getAllInvestors(pageableRequest.getPage(), pageableRequest.getSize()));

    return ResponseEntity.ok(response);
  }
}
