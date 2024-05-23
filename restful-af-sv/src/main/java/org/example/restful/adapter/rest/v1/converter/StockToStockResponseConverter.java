package org.example.restful.adapter.rest.v1.converter;

import org.example.restful.domain.Stock;
import org.openapitools.model.StockResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StockToStockResponseConverter implements Converter<Stock, StockResponse> {

  @Override
  public StockResponse convert(Stock domain) {
    if (domain == null) {
      return null;
    } else {
      return StockResponse.builder()
          .isin(domain.getIsin())
          .corporationName(domain.getCorporationName())
          .market(domain.getMarket())
          .currency(domain.getCurrency())
          .build();
    }
  }

  public List<StockResponse> convert(List<Stock> domains) {
    if (domains == null || domains.isEmpty()) {
      return Collections.emptyList();
    } else {
      return domains.stream().map(p -> convert(p)).collect(Collectors.toList());
    }
  }

  public Map<String, String> convertLinks(Map<RequestMethod, WebMvcLinkBuilder> hateoasMap) {
    return hateoasMap.entrySet().stream()
        .map(m -> m.getValue().withRel(m.getKey().name().toLowerCase()))
        .collect(Collectors.toMap(l -> l.getRel().toString(), Link::getHref));
  }
}
