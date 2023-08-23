package org.example.restful.port.repository;

import java.util.Optional;

import org.example.restful.port.repository.entity.InvestorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InvestorRepository extends PagingAndSortingRepository<InvestorEntity, Long> {
  Optional<InvestorEntity> findByIdNumber(String idNumber);

  @Override
  Page<InvestorEntity> findAll(Pageable pageable);
}
