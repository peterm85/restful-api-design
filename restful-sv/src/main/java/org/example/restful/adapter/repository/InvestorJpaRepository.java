package org.example.restful.adapter.repository;

import org.example.restful.adapter.repository.entity.InvestorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InvestorJpaRepository extends PagingAndSortingRepository<InvestorEntity, Long> {

  @Override
  Page<InvestorEntity> findAll(Pageable pageable);
}
