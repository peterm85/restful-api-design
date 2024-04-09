package org.example.restful.port.rest.v2.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Links {

  private String self;

  private String prev;

  private String next;

  private String first;
}
