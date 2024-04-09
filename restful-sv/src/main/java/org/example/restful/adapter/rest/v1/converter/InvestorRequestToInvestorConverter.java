package org.example.restful.adapter.rest.v1.converter;

import org.example.restful.domain.Investor;
import org.example.restful.port.rest.v1.api.model.InvestorRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class InvestorRequestToInvestorConverter implements Converter<InvestorRequest, Investor> {

  @Override
  public Investor convert(InvestorRequest request) {
    if (request == null) {
      return null;
    } else {
      return Investor.builder()
          .idNumber(request.getIdNumber())
          .name(request.getName())
          .age(request.getAge())
          .country(request.getCountry())
          .build();
    }
  }
}
