package org.example.restful.service;

import org.example.restful.domain.Operation;
import org.example.restful.domain.dtos.CreateOperationDto;
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
  public Operation purchase(
      final CreateOperationDto createOperationDto, final Long investorId, final String stockIsin) {
    log.info("Purchasing shares from investor {}", investorId);

    final Operation operation =
        Operation.create(
            createOperationDto,
            investorService.getInvestorById(investorId),
            stockService.getStockByIsin(stockIsin));

    return tradingRepository.purchase(operation);
  }
}
