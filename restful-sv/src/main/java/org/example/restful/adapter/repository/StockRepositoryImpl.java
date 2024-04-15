package org.example.restful.adapter.repository;

import org.example.restful.adapter.repository.converter.StockEntityToStockConverter;
import org.example.restful.adapter.repository.converter.StockToStockEntityConverter;
import org.example.restful.adapter.repository.entity.StockEntity;
import org.example.restful.domain.Stock;
import org.example.restful.exception.StockException;
import org.example.restful.exception.StockNotFoundException;
import org.example.restful.port.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StockRepositoryImpl implements StockRepository {

  @Autowired private StockJpaRepository stockJpaRepository;

  @Autowired private StockEntityToStockConverter entityConverter;
  @Autowired private StockToStockEntityConverter domainConverter;

  @Override
  public Stock getByIsin(final String isin) {
    return stockJpaRepository
        .findByIsin(isin)
        .map(entityConverter::convert)
        .orElseThrow(StockNotFoundException::new);
  }

  @Override
  public List<Stock> getAll() {
    return entityConverter.convert(stockJpaRepository.findAll());
  }

  @Override
  public Stock save(final Stock stock) {
    try {
      final StockEntity newEntity = stockJpaRepository.save(domainConverter.convert(stock));

      return entityConverter.convert(newEntity);
    } catch (DataIntegrityViolationException e) {
      throw new StockException(String.format("Stock '%s' already exists", stock.getIsin()));
    }
  }
}
