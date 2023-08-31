package org.example.restful.adapter.rest.v1.controller;

import org.example.restful.port.rest.v1.api.model.StockRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.example.restful.adapter.rest.v1.controller.StockControllerImpl.PATH;
import static org.example.restful.adapter.rest.v1.controller.StockControllerImpl.SUBPATH;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class StockControllerImplIntegrationTest {

  @Autowired private MockMvc mvc;

  private static ObjectMapper mapper = new ObjectMapper();

  @Test
  @WithMockUser(roles = "USER")
  @SqlGroup({
    @Sql(
        value = "classpath:init/data-stock.sql",
        config = @SqlConfig(encoding = "utf-8"),
        executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  })
  public void whenGetAllStocks_thenStatus200() throws Exception {

    // when then
    mvc.perform(get(PATH + SUBPATH).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].corporationName", is("Singular People")))
        .andExpect(jsonPath("$[1].corporationName", is("BBVA")))
        .andExpect(jsonPath("$[2].corporationName", is("Telefónica")))
        .andExpect(header().string("Cache-Control", equalTo("max-age=70")))
        .andExpect(header().string("X-Last-Modified", notNullValue()));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
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
        .andExpect(jsonPath("$._links", hasKey("GET")));
  }

  @Test
  @WithMockUser(roles = "USER")
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
