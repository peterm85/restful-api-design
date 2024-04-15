package org.example.restful.adapter.rest;

import org.example.restful.configuration.CacheTTL;
import org.example.restful.port.rest.v2.api.model.Links;
import org.example.restful.port.rest.v2.api.model.PaginationLinks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;
import java.util.Optional;

public abstract class HateoasUtils<T extends RepresentationModel<T>> {

  public static final Integer PAGINATION_DEFAULT_LIMIT = 30;

  public static final Long PAGINATION_DEFAULT_OFFSET = 0L;

  private static final String OFFSET = "offset";

  @Autowired protected CacheTTL cacheTTL;

  protected void applyHATEOAS(T response, Map<RequestMethod, WebMvcLinkBuilder> hateoasMap) {

    hateoasMap
        .entrySet()
        .forEach(m -> response.add(m.getValue().withRel(m.getKey().name().toLowerCase())));
  }

  protected PageRequest getPageable(final Optional<Long> offset, final Optional<Integer> limit) {
    return PageRequest.of(
        (int) (offset.orElse(PAGINATION_DEFAULT_OFFSET) / limit.orElse(PAGINATION_DEFAULT_LIMIT)),
        limit.orElse(PAGINATION_DEFAULT_LIMIT));
  }

  protected PaginationLinks getPagination(
      final Long offset, final Integer limit, final Long total) {
    return PaginationLinks.builder()
        .limit(limit)
        .offset(offset)
        .total(total)
        ._links(
            Links.builder()
                .first(
                    ServletUriComponentsBuilder.fromCurrentRequest()
                        .replaceQueryParam(OFFSET, 0L)
                        .build()
                        .toUriString())
                .self(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString())
                .next(
                    offset + limit >= total
                        ? null
                        : ServletUriComponentsBuilder.fromCurrentRequest()
                            .replaceQueryParam(OFFSET, offset + limit)
                            .build()
                            .toUriString())
                .prev(
                    offset - limit < 0
                        ? null
                        : ServletUriComponentsBuilder.fromCurrentRequest()
                            .replaceQueryParam(OFFSET, offset - limit)
                            .build()
                            .toUriString())
                .build())
        .build();
  }
}
