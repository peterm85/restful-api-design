package org.example.restful.port.repository;

import org.example.restful.domain.Stock;

import java.util.List;

public interface StockRepository {

  Stock getByIsin(String isin);

  List<Stock> getAll();

  Stock save(Stock stock);
}
