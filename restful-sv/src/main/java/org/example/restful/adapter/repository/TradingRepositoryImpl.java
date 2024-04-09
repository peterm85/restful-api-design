package org.example.restful.adapter.repository;

import org.example.restful.adapter.repository.converter.OperationEntityToOperationConverter;
import org.example.restful.adapter.repository.converter.OperationToOperationEntityConverter;
import org.example.restful.adapter.repository.entity.OperationEntity;
import org.example.restful.domain.Operation;
import org.example.restful.port.repository.TradingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TradingRepositoryImpl implements TradingRepository {

  @Autowired private TradingJpaRepository tradingJpaRepository;

  @Autowired private OperationToOperationEntityConverter operationToEntityConverter;
  @Autowired private OperationEntityToOperationConverter purchaseOrderEntityConverter;

  @Override
  public Operation purchase(final Operation operation) {

    final OperationEntity operationEntity =
        tradingJpaRepository.save(operationToEntityConverter.convert(operation));

    return purchaseOrderEntityConverter.convert(operationEntity);
  }
}
