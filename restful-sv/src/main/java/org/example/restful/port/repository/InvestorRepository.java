package org.example.restful.port.repository;

import org.example.restful.port.repository.entity.InvestorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InvestorRepository extends PagingAndSortingRepository<InvestorEntity, Long> {

  @Override
  Page<InvestorEntity> findAll(Pageable pageable);
}
