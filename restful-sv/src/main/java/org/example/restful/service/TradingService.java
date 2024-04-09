package org.example.restful.service;

import org.example.restful.adapter.repository.converter.OperationEntityToOperationConverter;
import org.example.restful.adapter.repository.converter.OperationToOperationEntityConverter;
import org.example.restful.domain.Investor;
import org.example.restful.domain.Operation;
import org.example.restful.domain.Stock;
import org.example.restful.port.repository.TradingRepository;
import org.example.restful.port.repository.entity.OperationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TradingService {

  @Autowired private InvestorService investorService;
  @Autowired private StockService stockService;

  @Autowired private TradingRepository tradingRepository;

  @Autowired private OperationToOperationEntityConverter operationToEntityConverter;
  @Autowired private OperationEntityToOperationConverter purchaseOrderEntityConverter;

  @Transactional
  public Operation purchase(final Long id, final Operation operation) {
    log.info("Buying");

    final Investor investor = investorService.getInvestorById(id);
    final Stock stock = stockService.getStockByIsin(operation.getIsin());

    final OperationEntity newPurchaseOrder =
        tradingRepository.save(operationToEntityConverter.convert(operation, investor, stock));

    return purchaseOrderEntityConverter.convert(newPurchaseOrder);
  }
}
