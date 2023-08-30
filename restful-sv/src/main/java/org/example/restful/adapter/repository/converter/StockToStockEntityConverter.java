package org.example.restful.adapter.repository.converter;

import org.example.restful.domain.Stock;
import org.example.restful.port.repository.entity.StockEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StockToStockEntityConverter implements Converter<Stock, StockEntity> {

  @Override
  public StockEntity convert(Stock domain) {
    if (domain == null) {
      return null;
    } else {
      return StockEntity.builder()
          .id(domain.getId())
          .isin(domain.getIsin())
          .corporationName(domain.getCorporationName())
          .market(domain.getMarket())
          .currency(domain.getCurrency())
          .build();
    }
  }
}
