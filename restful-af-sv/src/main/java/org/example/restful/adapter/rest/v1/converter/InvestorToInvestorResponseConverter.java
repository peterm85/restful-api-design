package org.example.restful.adapter.rest.v1.converter;

import org.example.restful.domain.Investor;
import org.openapitools.model.InvestorResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InvestorToInvestorResponseConverter implements Converter<Investor, InvestorResponse> {

  @Override
  public InvestorResponse convert(Investor domain) {
    if (domain == null) {
      return null;
    } else {
      return InvestorResponse.builder()
          .id(domain.getId())
          .idNumber(domain.getIdNumber())
          .name(domain.getName())
          .age(domain.getAge())
          .country(domain.getCountry())
          .build();
    }
  }

  public Map<String, String> convertLinks(Map<RequestMethod, WebMvcLinkBuilder> hateoasMap) {
    return hateoasMap.entrySet().stream()
        .map(m -> m.getValue().withRel(m.getKey().name().toLowerCase()))
        .collect(Collectors.toMap(l -> l.getRel().toString(), Link::getHref));
  }
}
