package org.example.restful.adapter.repository;

import org.example.restful.adapter.repository.entity.OperationEntity;
import org.springframework.data.repository.CrudRepository;

public interface TradingJpaRepository extends CrudRepository<OperationEntity, Long> {}
