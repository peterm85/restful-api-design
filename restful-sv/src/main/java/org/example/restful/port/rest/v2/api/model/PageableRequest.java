package org.example.restful.port.rest.v2.api.model;

import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class PageableRequest {

  @Min(0)
  private int page;

  @Min(1)
  private int size;
}
