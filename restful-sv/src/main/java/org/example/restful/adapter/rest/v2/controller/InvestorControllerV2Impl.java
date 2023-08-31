package org.example.restful.adapter.rest.v2.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.example.restful.adapter.rest.RestfulAPIController;
import org.example.restful.adapter.rest.v1.controller.InvestorControllerImpl;
import org.example.restful.adapter.rest.v1.converter.InvestorToInvestorResponseConverter;
import org.example.restful.port.rest.v1.api.model.InvestorRequest;
import org.example.restful.port.rest.v1.api.model.InvestorResponse;
import org.example.restful.port.rest.v2.InvestorControllerV2;
import org.example.restful.port.rest.v2.api.model.PageableRequest;
import org.example.restful.service.InvestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.example.restful.constant.Roles.ADMIN;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(InvestorControllerV2Impl.PATH)
public class InvestorControllerV2Impl extends RestfulAPIController<InvestorResponse>
    implements InvestorControllerV2 {

  public static final String PATH = "/api/v2/invest";
  public static final String SLASH = "/";
  public static final String SUBPATH = SLASH + "investor";

  @Autowired private InvestorService investorService;

  @Autowired private InvestorToInvestorResponseConverter converter;

  @Override
  @RolesAllowed(ADMIN)
  @GetMapping(value = SUBPATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<InvestorResponse>> getAllInvestors(
      @Valid final PageableRequest pageableRequest) {

    final List<InvestorResponse> response =
        converter.convert(
            investorService.getAllInvestors(pageableRequest.getPage(), pageableRequest.getSize()));

    applyHATEOAS(response, List.of(PUT, DELETE));

    return ResponseEntity.ok(response);
  }

  @Override
  protected Map<RequestMethod, WebMvcLinkBuilder> hateoasMap(InvestorResponse response) {
    Map<RequestMethod, WebMvcLinkBuilder> hateoasMap =
        new HashMap<RequestMethod, WebMvcLinkBuilder>();

    hateoasMap.put(
        PUT,
        linkTo(
            methodOn(InvestorControllerImpl.class)
                .updateInvestor(InvestorRequest.builder().build())));
    hateoasMap.put(
        DELETE,
        linkTo(methodOn(InvestorControllerImpl.class).deleteInvestor(response.getIdNumber())));

    return hateoasMap;
  }
}
