package org.example.restful.adapter.repository;

import org.example.restful.adapter.repository.converter.InvestorEntityToInvestorConverter;
import org.example.restful.adapter.repository.converter.InvestorToInvestorEntityConverter;
import org.example.restful.adapter.repository.entity.InvestorEntity;
import org.example.restful.domain.Investor;
import org.example.restful.exception.InvestorException;
import org.example.restful.exception.InvestorNotFoundException;
import org.example.restful.port.repository.InvestorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class InvestorRepositoryImpl implements InvestorRepository {

  @Autowired private InvestorJpaRepository investorJpaRepository;

  @Autowired private InvestorEntityToInvestorConverter entityConverter;
  @Autowired private InvestorToInvestorEntityConverter domainConverter;

  @Override
  public Page<Investor> findAll(final Pageable pageable) {

    return investorJpaRepository.findAll(pageable).map(entityConverter::convert);
  }

  @Override
  public Investor getById(final Long id) {
    return investorJpaRepository
        .findById(id)
        .map(entityConverter::convert)
        .orElseThrow(InvestorNotFoundException::new);
  }

  @Override
  public Investor save(final Investor investor) {
    try {
      final InvestorEntity savedInvestor =
          investorJpaRepository.save(domainConverter.convert(investor));

      return entityConverter.convert(savedInvestor);
    } catch (DataIntegrityViolationException e) {
      throw new InvestorException(
          String.format("Investor '%s' already exists", investor.getIdNumber()));
    }
  }

  @Override
  public void delete(final Long id) {
    investorJpaRepository.deleteById(id);
  }
}
