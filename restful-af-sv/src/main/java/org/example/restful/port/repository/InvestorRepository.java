package org.example.restful.port.repository;

import org.example.restful.domain.Investor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvestorRepository {

  Investor getById(Long id);

  Page<Investor> findAll(Pageable pageable);

  Investor save(Investor investor);

  void delete(Long id);
}
