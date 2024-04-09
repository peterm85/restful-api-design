package org.example.restful.adapter.repository.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.example.restful.domain.Stock;
import org.example.restful.port.repository.entity.StockEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

@Component
public class StockEntityToStockConverter implements Converter<StockEntity, Stock> {

  @Override
  public Stock convert(StockEntity entity) {
    if (entity == null) {
      return null;
    } else {
      return Stock.builder()
          .id(entity.getId())
          .isin(entity.getIsin())
          .corporationName(entity.getCorporationName())
          .market(entity.getMarket())
          .currency(entity.getCurrency())
          .build();
    }
  }

  public List<Stock> convert(Iterable<StockEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    } else {
      return ImmutableList.copyOf(entities).stream()
          .map(entity -> convert(entity))
          .collect(Collectors.toList());
    }
  }
}
