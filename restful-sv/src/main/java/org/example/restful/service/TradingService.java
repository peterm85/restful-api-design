package org.example.restful.service;

import org.example.restful.domain.Operation;
import org.example.restful.port.repository.TradingRepository;
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

  @Transactional
  public Operation purchase(final Long id, final Operation operation) {
    log.info("Buying");

    operation.setInvestor(investorService.getInvestorById(id));
    operation.setStock(stockService.getStockByIsin(operation.getStock().getIsin()));

    return tradingRepository.purchase(operation);
  }
}
