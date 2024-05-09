package org.example.restful.adapter.rest.v1.converter;

import org.example.restful.domain.Stock;
import org.example.restful.port.rest.v1.api.model.StockRequest;
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
