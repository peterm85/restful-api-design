package org.example.restful.adapter.rest.investor;

import org.example.restful.port.rest.v1.api.model.InvestorRequest;
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

import static org.example.restful.adapter.rest.v1.controller.InvestorControllerImpl.PATH;
import static org.example.restful.adapter.rest.v1.controller.InvestorControllerImpl.SUBPATH;
import static org.example.restful.constant.Roles.USER;
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
public class CreateInvestorRestIT {

  @Autowired private MockMvc mvc;

  private static ObjectMapper mapper = new ObjectMapper();

  @Test
  @WithMockUser(roles = USER)
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

    // when then
    mvc.perform(
            post(PATH + SUBPATH).contentType(MediaType.APPLICATION_JSON).content(asJson(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.idNumber", is("11222333X")))
        .andExpect(jsonPath("$.name", is("Olga Carmona")))
        .andExpect(jsonPath("$._links", hasKey("GET")))
        .andExpect(jsonPath("$._links", hasKey("PUT")))
        .andExpect(jsonPath("$._links", hasKey("DELETE")));
  }

  @Test
  @WithMockUser(roles = USER)
  @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  public void givenAnAlreadyCreatedInvestor_whenCreateInvestor_thenStatus400() throws Exception {
    // given
    final InvestorRequest request =
        InvestorRequest.builder()
            .idNumber("76245691H")
            .name("Manuel Rodriguez")
            .age(37)
            .country("SPAIN")
            .build();

    // when then
    mvc.perform(
            post(PATH + SUBPATH).contentType(MediaType.APPLICATION_JSON).content(asJson(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.idNumber", is("76245691H")))
        .andExpect(jsonPath("$.name", is("Manuel Rodriguez")));

    mvc.perform(
            post(PATH + SUBPATH).contentType(MediaType.APPLICATION_JSON).content(asJson(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  @WithMockUser(roles = "USER")
  public void givenIncompletedBodyRequest_whenCreateInvestor_thenStatus400() throws Exception {
    // given
    final InvestorRequest request = InvestorRequest.builder().idNumber("11222333X").build();

    // when then
    mvc.perform(
            post(PATH + SUBPATH).contentType(MediaType.APPLICATION_JSON).content(asJson(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  private static String asJson(Object obj) throws JsonProcessingException {
    return mapper.writeValueAsString(obj);
  }
}
