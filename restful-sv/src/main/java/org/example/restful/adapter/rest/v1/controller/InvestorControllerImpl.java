package org.example.restful.adapter.rest.v1.controller;

import java.util.Collections;
import java.util.List;

import org.example.restful.adapter.rest.v1.converter.InvestorRequestToInvestorConverter;
import org.example.restful.adapter.rest.v1.converter.InvestorToInvestorResponseConverter;
import org.example.restful.domain.Investor;
import org.example.restful.port.rest.v1.InvestorController;
import org.example.restful.port.rest.v1.api.model.InvestorRequest;
import org.example.restful.port.rest.v1.api.model.InvestorResponse;
import org.example.restful.service.InvestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(InvestorControllerImpl.PATH)
public class InvestorControllerImpl implements InvestorController {

  public static final String PATH = "/api/v1/invest";
  public static final String NEW_PATH = "/api/v2/invest";
  public static final String SLASH = "/";
  public static final String SUBPATH = SLASH + "investor";
  private static final String ID_PATH_PARAM = SLASH + "{idNumber}";

  @Autowired private InvestorService investorService;

  @Autowired private InvestorToInvestorResponseConverter responseConverter;
  @Autowired private InvestorRequestToInvestorConverter requestConverter;

  @Override
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  @GetMapping(
      value = SUBPATH + ID_PATH_PARAM,
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<InvestorResponse> getInvestor(@PathVariable final String idNumber) {

    final InvestorResponse response =
        responseConverter.convert(investorService.getInvestorByIdNumber(idNumber));

    return ResponseEntity.ok().body(response);
  }

  @SuppressWarnings("unchecked")
  @Override
  @Deprecated
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

  @Override
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  @PostMapping(
      value = SUBPATH,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InvestorResponse> createInvestor(
      @RequestBody final InvestorRequest investorRequest) throws Exception {

    final Investor investor =
        investorService.createInvestor(requestConverter.convert(investorRequest));

    final InvestorResponse response = responseConverter.convert(investor);

    return ResponseEntity.status(HttpStatus.CREATED).body(applyHATEOAS(investorRequest, response));
  }

  @SuppressWarnings("rawtypes")
  @Override
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  @PutMapping(value = SUBPATH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity updateInvestor(@RequestBody final InvestorRequest investorRequest) {

    investorService.updateInvestor(requestConverter.convert(investorRequest));

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @SuppressWarnings("rawtypes")
  @Override
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  @DeleteMapping(value = SUBPATH + ID_PATH_PARAM, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity deleteInvestor(@PathVariable final String idNumber) {

    investorService.deleteInvestor(idNumber);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  private InvestorResponse applyHATEOAS(
      InvestorRequest investorRequest, InvestorResponse response) {

    response.add(
        linkTo(methodOn(InvestorControllerImpl.class).getInvestor(investorRequest.getIdNumber()))
            .withRel(RequestMethod.GET.name()));
    response.add(
        linkTo(methodOn(InvestorControllerImpl.class).updateInvestor(investorRequest))
            .withRel(RequestMethod.PUT.name()));
    response.add(
        linkTo(methodOn(InvestorControllerImpl.class).deleteInvestor(investorRequest.getIdNumber()))
            .withRel(RequestMethod.DELETE.name()));

    return response;
  }
}
