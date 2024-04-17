package org.example.restful.adapter.rest.stock;

import org.example.restful.port.rest.v1.api.model.StockRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.example.restful.adapter.rest.v1.controller.StockControllerImpl.PATH;
import static org.example.restful.adapter.rest.v1.controller.StockControllerImpl.SUBPATH;
import static org.example.restful.constant.Roles.ADMIN;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class CreateStockRestIT {

  @Autowired private MockMvc mvc;

  private static ObjectMapper mapper = new ObjectMapper();

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
            post(PATH + SUBPATH).contentType(MediaType.APPLICATION_JSON).content(asJson(request)))
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
            post(PATH + SUBPATH).contentType(MediaType.APPLICATION_JSON).content(asJson(request)))
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
            post(PATH + SUBPATH).contentType(MediaType.APPLICATION_JSON).content(asJson(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  private static String asJson(Object obj) throws JsonProcessingException {
    return mapper.writeValueAsString(obj);
  }
}
