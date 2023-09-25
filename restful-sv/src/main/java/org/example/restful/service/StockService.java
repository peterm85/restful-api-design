package org.example.restful.service;

import java.util.List;

import org.example.restful.adapter.repository.converter.StockEntityToStockConverter;
import org.example.restful.adapter.repository.converter.StockToStockEntityConverter;
import org.example.restful.domain.Stock;
import org.example.restful.exception.StockNotFoundException;
import org.example.restful.port.repository.StockRepository;
import org.example.restful.port.repository.entity.StockEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class StockService {

  @Autowired private StockRepository stockRepository;

  @Autowired private StockEntityToStockConverter entityConverter;
  @Autowired private StockToStockEntityConverter domainConverter;

  public Stock getStockByIsin(final String isin) {

    return stockRepository
        .findByIsin(isin)
        .map(entityConverter::convert)
        .orElseThrow(StockNotFoundException::new);
  }

  public List<Stock> getAllStocks() {
    return entityConverter.convert(stockRepository.findAll());
  }

  public Stock createStock(final Stock stock) {

    try {
      final StockEntity newEntity = stockRepository.save(domainConverter.convert(stock));

      return entityConverter.convert(newEntity);
    } catch (DataIntegrityViolationException e) {
      if (e.getMessage().contains(StockEntity.UNIQUE_ISIN_CONSTRAINT)) {
        return getStockByIsin(stock.getIsin());
      } else {
        throw e;
      }
    }
  }
}
