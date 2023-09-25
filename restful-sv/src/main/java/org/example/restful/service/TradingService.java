package org.example.restful.service;

import javax.transaction.Transactional;

import org.example.restful.adapter.repository.converter.OperationEntityToOperationConverter;
import org.example.restful.adapter.repository.converter.OperationToOperationEntityConverter;
import org.example.restful.domain.Investor;
import org.example.restful.domain.Operation;
import org.example.restful.domain.Stock;
import org.example.restful.port.repository.TradingRepository;
import org.example.restful.port.repository.entity.OperationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
  public Operation purchase(String idNumber, Operation operation) {
    log.info("Buying");

    final Investor investor = investorService.getInvestorByIdNumber(idNumber);
    final Stock stock = stockService.getStockByIsin(operation.getIsin());

    final OperationEntity newPurchaseOrder =
        tradingRepository.save(operationToEntityConverter.convert(operation, investor, stock));

    return purchaseOrderEntityConverter.convert(newPurchaseOrder);
  }
}
