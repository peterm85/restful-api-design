package org.example.restful.adapter.rest.stock;

import org.example.restful.adapter.rest.AbstractRestIT;
import org.junit.jupiter.api.Test;
import org.openapitools.model.StockRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;

import static org.example.restful.constant.Roles.ADMIN;
import static org.example.restful.constant.UrlConstants.BASE_PATH_V1;
import static org.example.restful.constant.UrlConstants.STOCKS_SUBPATH;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CreateStockRestIT extends AbstractRestIT {

  @Test
  @WithMockUser(roles = ADMIN)
  @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  public void whenCreateStock_thenStatus201() throws Exception {
    // given
    final StockRequest request =
        StockRequest.builder()
            .isin("ES0105611000")
            .corporationName("Singular People")
            .market("MAD")
            .currency("EUR")
            .build();

    // when then
    mvc.perform(
            post(BASE_PATH_V1 + STOCKS_SUBPATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.isin", is("ES0105611000")))
        .andExpect(jsonPath("$.corporationName", is("Singular People")))
        .andExpect(jsonPath("$._links", hasKey("get")))
        .andExpect(jsonPath("$._links", hasKey("patch")));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void givenIncompletedBodyRequest_whenCreateStock_thenStatus400() throws Exception {
    // given
    final StockRequest request = StockRequest.builder().isin("ES0105611000").build();

    // when then
    mvc.perform(
            post(BASE_PATH_V1 + STOCKS_SUBPATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  @WithMockUser(roles = "INVALID")
  public void givenInvalidAuthRole_whenCreateStock_thenStatus401() throws Exception {
    // given
    final StockRequest request =
        StockRequest.builder()
            .isin("ES0105611000")
            .corporationName("Singular People")
            .market("MAD")
            .currency("EUR")
            .build();

    // when then
    mvc.perform(
            post(BASE_PATH_V1 + STOCKS_SUBPATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }
}
