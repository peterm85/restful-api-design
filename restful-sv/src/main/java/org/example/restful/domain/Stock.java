package org.example.restful.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
  private Long id;
  private String isin;
  private String corporationName;
  private String market;
  private String currency;
}
