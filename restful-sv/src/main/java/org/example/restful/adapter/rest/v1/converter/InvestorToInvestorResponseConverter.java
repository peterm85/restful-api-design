package org.example.restful.adapter.rest.v1.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.example.restful.domain.Investor;
import org.example.restful.port.rest.v1.api.model.InvestorResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class InvestorToInvestorResponseConverter implements Converter<Investor, InvestorResponse> {

  @Override
  public InvestorResponse convert(Investor domain) {
    if (domain == null) {
      return null;
    } else {
      return InvestorResponse.builder()
          .idNumber(domain.getIdNumber())
          .name(domain.getName())
          .age(domain.getAge())
          .country(domain.getCountry())
          .build();
    }
  }

  public List<InvestorResponse> convert(List<Investor> domains) {
    if (domains == null || domains.isEmpty()) {
      return Collections.emptyList();
    } else {
      return domains.stream().map(p -> convert(p)).collect(Collectors.toList());
    }
  }
}
