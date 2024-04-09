package org.example.restful.port.repository;

import org.example.restful.adapter.repository.entity.OperationEntity;
import org.springframework.data.repository.CrudRepository;

public interface TradingRepository extends CrudRepository<OperationEntity, Long> {}
