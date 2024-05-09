package org.example.restful.service;

import org.example.restful.domain.Investor;
import org.example.restful.port.repository.InvestorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InvestorService {

  @Autowired private InvestorRepository investorRepository;

  public Investor getInvestorById(final Long id) {

    return investorRepository.getById(id);
  }

  public Page<Investor> getAllInvestors(final Pageable pageable) {

    return investorRepository.findAll(pageable);
  }

  public Investor createInvestor(final Investor investor) throws Exception {

    return investorRepository.save(investor);
  }

  public void updateInvestor(final Long id, final Investor investor) {

    investorRepository.getById(id);
    investor.setId(id);
    investorRepository.save(investor);
  }

  public void deleteInvestor(final Long id) {

    investorRepository.delete(id);
  }
}
