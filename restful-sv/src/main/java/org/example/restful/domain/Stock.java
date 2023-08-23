package org.example.restful.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Stock {
  private String iSIN;
  private String companyName;
  private Integer quantity;
}
