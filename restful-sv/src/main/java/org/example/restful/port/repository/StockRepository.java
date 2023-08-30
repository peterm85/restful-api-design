package org.example.restful.port.repository;

import java.util.Optional;

import org.example.restful.port.repository.entity.StockEntity;
import org.springframework.data.repository.CrudRepository;

public interface StockRepository extends CrudRepository<StockEntity, Long> {

  Optional<StockEntity> findByIsin(String isin);
}
