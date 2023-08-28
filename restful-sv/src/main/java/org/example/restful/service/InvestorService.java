package org.example.restful.service;

import java.util.List;

import org.example.restful.adapter.repository.converter.InvestorEntityToInvestorConverter;
import org.example.restful.adapter.repository.converter.InvestorToInvestorEntityConverter;
import org.example.restful.domain.Investor;
import org.example.restful.exception.NotFoundException;
import org.example.restful.port.repository.InvestorRepository;
import org.example.restful.port.repository.entity.InvestorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InvestorService {

  @Autowired private InvestorRepository investorRepository;

  @Autowired private InvestorEntityToInvestorConverter entityConverter;
  @Autowired private InvestorToInvestorEntityConverter domainConverter;

  public Investor getInvestorByIdNumber(final String idNumber) {

    return investorRepository
        .findByIdNumber(idNumber)
        .map(entityConverter::convert)
        .orElseThrow(NotFoundException::new);
  }

  public List<Investor> getAllInvestors(int page, int size) {

    final Pageable pageable = PageRequest.of(page, size);

    return entityConverter.convert(investorRepository.findAll(pageable));
  }

  public Investor createInvestor(final Investor investor) throws Exception {

    try {
      final InvestorEntity newEntity = investorRepository.save(domainConverter.convert(investor));

      return entityConverter.convert(newEntity);
    } catch (DataIntegrityViolationException e) {
      if (e.getMessage().contains(InvestorEntity.UNIQUE_ID_NUMBER_CONSTRAINT)) {
        return getInvestorByIdNumber(investor.getIdNumber());
      } else {
        throw e;
      }
    }
  }

  public void updateInvestor(final Investor investor) {

    Investor investorFromDb =
        investorRepository
            .findByIdNumber(investor.getIdNumber())
            .map(entityConverter::convert)
            .orElseThrow(NotFoundException::new);

    investor.setId(investorFromDb.getId());

    investorRepository.save(domainConverter.convert(investor));
  }

  public void deleteInvestor(final String idNumber) {

    try {
      final InvestorEntity investor =
          investorRepository.findByIdNumber(idNumber).orElseThrow(NotFoundException::new);

      investorRepository.delete(investor);
    } catch (NotFoundException nfe) {
      log.debug("Investor {} not found", idNumber);
    }
  }
}
