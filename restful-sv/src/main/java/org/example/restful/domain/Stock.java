package org.example.restful.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Stock {
  private Long id;
  private String isin;
  private String corporationName;
  private String market;
  private String currency;
}
