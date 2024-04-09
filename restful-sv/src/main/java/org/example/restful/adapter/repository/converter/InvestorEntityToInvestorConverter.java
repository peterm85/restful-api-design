package org.example.restful.adapter.repository.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.example.restful.domain.Investor;
import org.example.restful.port.repository.entity.InvestorEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

@Component
public class InvestorEntityToInvestorConverter implements Converter<InvestorEntity, Investor> {

  @Override
  public Investor convert(InvestorEntity entity) {
    if (entity == null) {
      return null;
    } else {
      return Investor.builder()
          .id(entity.getId())
          .idNumber(entity.getIdNumber())
          .name(entity.getName())
          .age(entity.getAge())
          .country(entity.getCountry())
          .build();
    }
  }

  public List<Investor> convert(Page<InvestorEntity> page) {

    if (page.getSize() == 0) {
      return Collections.emptyList();
    } else {
      List<InvestorEntity> entities = page.getContent();
      return ImmutableList.copyOf(entities).stream()
          .map(entity -> convert(entity))
          .collect(Collectors.toList());
    }
  }
}
