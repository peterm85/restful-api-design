package org.example.restful.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Investor {
  private String idNumber;
  private String name;
  private Integer age;
  private String country;
}
