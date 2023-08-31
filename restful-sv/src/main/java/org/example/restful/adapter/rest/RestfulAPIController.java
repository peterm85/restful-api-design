package org.example.restful.adapter.rest;

import java.util.List;
import java.util.Map;

import org.example.restful.configuration.CacheTTL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class RestfulAPIController<T extends RepresentationModel<T>> {

  @Autowired protected CacheTTL cacheTTL;

  protected void applyHATEOAS(T response, List<RequestMethod> includedLinks) {

    includedLinks.stream()
        .forEach(m -> response.add(hateoasMap(response).get(m).withRel(m.name())));
  }

  protected void applyHATEOAS(List<T> response, List<RequestMethod> includedLinks) {

    response.stream().forEach(r -> applyHATEOAS(r, includedLinks));
  }

  protected abstract Map<RequestMethod, WebMvcLinkBuilder> hateoasMap(T response);
}
