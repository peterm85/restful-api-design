package org.example.restful.adapter.rest.v1.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.example.restful.domain.Stock;
import org.example.restful.port.rest.v1.api.model.StockResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

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
}
