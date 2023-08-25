package org.example.restful.service;

import java.util.List;
import java.util.Optional;

import org.example.restful.adapter.repository.converter.InvestorEntityToInvestorConverter;
import org.example.restful.adapter.repository.converter.InvestorToInvestorEntityConverter;
import org.example.restful.domain.Investor;
import org.example.restful.exception.NotFoundException;
import org.example.restful.port.repository.InvestorRepository;
import org.example.restful.port.repository.entity.InvestorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InvestorService {

  @Autowired private InvestorRepository investorRepository;

  @Autowired private InvestorEntityToInvestorConverter entityConverter;
  @Autowired private InvestorToInvestorEntityConverter domainConverter;

  public Investor getInvestorByIdNumber(final String idNumber) {

    return entityConverter.convert(
        Optional.ofNullable(investorRepository.findByIdNumber(idNumber))
            .get()
            .orElseThrow(NotFoundException::new));
  }

  public List<Investor> getAllInvestors(int page, int size) {

    final Pageable pageable = PageRequest.of(page, size);

    return entityConverter.convert(investorRepository.findAll(pageable));
  }

  public Investor createInvestor(final Investor investor) {

    final InvestorEntity newEntity = investorRepository.save(domainConverter.convert(investor));

    return entityConverter.convert(newEntity);
  }
}
