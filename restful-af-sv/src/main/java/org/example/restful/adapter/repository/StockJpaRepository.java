package org.example.restful.adapter.repository;

import java.util.Optional;

import org.example.restful.adapter.repository.entity.StockEntity;
import org.springframework.data.repository.CrudRepository;

public interface StockJpaRepository extends CrudRepository<StockEntity, Long> {

  Optional<StockEntity> findByIsin(String isin);
}
