package org.example.restful.adapter.rest.v1.controller;

import org.example.restful.port.rest.v1.api.model.InvestorRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.example.restful.adapter.rest.v1.controller.InvestorControllerImpl.PATH;
import static org.example.restful.adapter.rest.v1.controller.InvestorControllerImpl.SLASH;
import static org.example.restful.adapter.rest.v1.controller.InvestorControllerImpl.SUBPATH;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class InvestorControllerImplIntegrationTest {

  @Autowired private MockMvc mvc;

  private static ObjectMapper mapper = new ObjectMapper();

  @Test
  @WithMockUser(roles = "USER")
  @SqlGroup({
    @Sql(value = "classpath:init/data-investor.sql", executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  })
  public void givenIdNumber_whenGetInvestorByIdNumber_thenStatus200() throws Exception {
    // given
    final String idNumber = "76245691H";

    // when then
    mvc.perform(get(PATH + SUBPATH + SLASH + idNumber).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.idNumber", is("76245691H")))
        .andExpect(jsonPath("$.name", is("Manuel Rodriguez")));
  }

  @Test
  @WithMockUser(roles = "INVALID")
  public void givenInvalidAuthRole_whenGetInvestorByIdNumber_thenStatus401() throws Exception {
    // given
    final String idNumber = "76245691H";

    // when then
    mvc.perform(get(PATH + SUBPATH + SLASH + idNumber).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  @WithMockUser(roles = "USER")
  @SqlGroup({
    @Sql(value = "classpath:init/data-investor.sql", executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  })
  public void givenWrongIdNumber_whenGetInvestorByIdNumber_thenStatus404() throws Exception {
    // given
    final String idNumber = "76245691Y";

    // when then
    mvc.perform(get(PATH + SUBPATH + SLASH + idNumber).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code", is("ERR404")))
        .andExpect(jsonPath("$.message", is("Investor not found")));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void whenGetAllInvestors_thenStatus409() throws Exception {
    // given
    final String expectedLocationRedirection = "/api/v2/invest/investor?page=0&size=3";

    // when then
    mvc.perform(get(PATH + SUBPATH).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isPermanentRedirect())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(header().string("location", equalTo(expectedLocationRedirection)))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @WithMockUser(roles = "USER")
  @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  public void whenCreateInvestor_thenStatus201() throws Exception {
    // given
    final InvestorRequest request =
        InvestorRequest.builder()
            .idNumber("11222333X")
            .name("Olga Carmona")
            .age(23)
            .country("GRECIA")
            .build();

    final String expectedLocationRedirection = "/api/v1/invest/investor/11222333X";

    // when then
    mvc.perform(
            post(PATH + SUBPATH).contentType(MediaType.APPLICATION_JSON).content(asJson(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(header().string("location", equalTo(expectedLocationRedirection)))
        .andExpect(jsonPath("$.idNumber", is("11222333X")))
        .andExpect(jsonPath("$.name", is("Olga Carmona")));
  }

  @Test
  @WithMockUser(roles = "USER")
  @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  public void givenAnAlreadyCreatedInvestor_whenCreateInvestor_thenStatus201() throws Exception {
    // given
    final InvestorRequest request =
        InvestorRequest.builder()
            .idNumber("76245691H")
            .name("Manuel Rodriguez")
            .age(37)
            .country("SPAIN")
            .build();

    final String expectedLocationRedirection = "/api/v1/invest/investor/76245691H";

    // when then
    mvc.perform(
            post(PATH + SUBPATH).contentType(MediaType.APPLICATION_JSON).content(asJson(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(header().string("location", equalTo(expectedLocationRedirection)))
        .andExpect(jsonPath("$.idNumber", is("76245691H")))
        .andExpect(jsonPath("$.name", is("Manuel Rodriguez")));

    mvc.perform(
            post(PATH + SUBPATH).contentType(MediaType.APPLICATION_JSON).content(asJson(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(header().string("location", equalTo(expectedLocationRedirection)))
        .andExpect(jsonPath("$.idNumber", is("76245691H")))
        .andExpect(jsonPath("$.name", is("Manuel Rodriguez")));
  }

  @Test
  @WithMockUser(roles = "USER")
  @SqlGroup({
    @Sql(value = "classpath:init/data-investor.sql", executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  })
  public void whenUpdateInvestor_thenStatus204() throws Exception {
    // given
    final String idNumber = "76245691H";

    final InvestorRequest request =
        InvestorRequest.builder()
            .idNumber(idNumber)
            .name("Manolo Rodriguez")
            .age(38)
            .country("SPAIN")
            .build();

    // when then
    mvc.perform(
            put(PATH + SUBPATH).contentType(MediaType.APPLICATION_JSON).content(asJson(request)))
        .andExpect(status().isNoContent());

    mvc.perform(get(PATH + SUBPATH + SLASH + idNumber).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.idNumber", is("76245691H")))
        .andExpect(jsonPath("$.name", is("Manolo Rodriguez")))
        .andExpect(jsonPath("$.age", is(38)));
  }

  @Test
  @WithMockUser(roles = "USER")
  public void givenNoInvestor_whenUpdateInvestor_thenStatus404() throws Exception {
    // given
    final String idNumber = "76245691H";

    final InvestorRequest request =
        InvestorRequest.builder()
            .idNumber(idNumber)
            .name("Manolo Rodriguez")
            .age(38)
            .country("SPAIN")
            .build();

    // when then
    mvc.perform(
            put(PATH + SUBPATH).contentType(MediaType.APPLICATION_JSON).content(asJson(request)))
        .andExpect(status().isNotFound());
  }

  private static String asJson(Object obj) throws JsonProcessingException {
    return mapper.writeValueAsString(obj);
  }
}
