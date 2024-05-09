package org.example.restful.port.rest.v2.api.model;

import org.example.restful.port.rest.v1.api.model.InvestorResponse;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvestorResponsePage {

  List<InvestorResponse> data;

  PaginationLinks pagination;
}
