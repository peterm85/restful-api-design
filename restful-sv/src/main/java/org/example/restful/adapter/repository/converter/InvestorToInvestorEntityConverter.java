package org.example.restful.adapter.repository.converter;

import org.example.restful.domain.Investor;
import org.example.restful.port.repository.entity.InvestorEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class InvestorToInvestorEntityConverter implements Converter<Investor, InvestorEntity> {

  @Override
  public InvestorEntity convert(Investor domain) {
    if (domain == null) {
      return null;
    } else {
      return InvestorEntity.builder()
          .id(domain.getId())
          .idNumber(domain.getIdNumber())
          .name(domain.getName())
          .age(domain.getAge())
          .country(domain.getCountry())
          .build();
    }
  }
}
