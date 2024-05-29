package org.example.restful.adapter.rest.converter;

import org.example.restful.domain.Stock;
import org.openapitools.model.StockRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StockRequestToStockConverter implements Converter<StockRequest, Stock> {

  @Override
  public Stock convert(StockRequest request) {
    if (request == null) {
      return null;
    } else {
      return Stock.builder()
          .isin(request.getIsin())
          .corporationName(request.getCorporationName())
          .market(request.getMarket())
          .currency(request.getCurrency())
          .build();
    }
  }
}
