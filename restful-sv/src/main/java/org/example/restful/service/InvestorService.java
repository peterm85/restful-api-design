package org.example.restful.service;

import org.example.restful.adapter.repository.converter.InvestorEntityToInvestorConverter;
import org.example.restful.adapter.repository.converter.InvestorToInvestorEntityConverter;
import org.example.restful.domain.Investor;
import org.example.restful.exception.InvestorException;
import org.example.restful.exception.InvestorNotFoundException;
import org.example.restful.port.repository.InvestorRepository;
import org.example.restful.port.repository.entity.InvestorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InvestorService {

  @Autowired private InvestorRepository investorRepository;

  @Autowired private InvestorEntityToInvestorConverter entityConverter;
  @Autowired private InvestorToInvestorEntityConverter domainConverter;

  public Investor getInvestorById(final Long id) {

    return investorRepository
        .findById(id)
        .map(entityConverter::convert)
        .orElseThrow(InvestorNotFoundException::new);
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
      throw new InvestorException(
          String.format("Investor {} already exists", investor.getIdNumber()));
    }
  }

  public void updateInvestor(final Long id, final Investor investor) {

    investorRepository.findById(id).orElseThrow(InvestorNotFoundException::new);

    investor.setId(id);
    investorRepository.save(domainConverter.convert(investor));
  }

  public void deleteInvestor(final Long id) {

    try {
      final InvestorEntity investor =
          investorRepository.findById(id).orElseThrow(InvestorNotFoundException::new);

      investorRepository.delete(investor);
    } catch (InvestorNotFoundException nfe) {
      log.debug("Investor {} not found", id);
    }
  }
}
