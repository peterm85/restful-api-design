package org.example.restful.port.rest.v2.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginationLinks {

  private Integer limit;

  private Long offset;

  private Long total;

  private Links links;
}
