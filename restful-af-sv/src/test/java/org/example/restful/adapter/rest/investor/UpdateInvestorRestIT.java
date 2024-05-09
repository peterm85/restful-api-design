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
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.example.restful.constant.Roles.USER;
import static org.example.restful.constant.UrlConstants.BASE_PATH_V1;
import static org.example.restful.constant.UrlConstants.INVESTORS_SUBPATH;
import static org.example.restful.constant.UrlConstants.SLASH;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class UpdateInvestorRestIT {

  @Autowired private MockMvc mvc;

  private static ObjectMapper mapper = new ObjectMapper();

  @Test
  @WithMockUser(roles = USER)
  @SqlGroup({
    @Sql(value = "classpath:init/data-investor.sql", executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  })
  public void whenUpdateInvestor_thenStatus204() throws Exception {
    // given
    final Long id = 1L;

    final InvestorRequest request =
        InvestorRequest.builder()
            .idNumber("76245691H")
            .name("Manolo Rodriguez")
            .age(38)
            .country("SPAIN")
            .build();

    // when then
    mvc.perform(
            put(BASE_PATH_V1 + INVESTORS_SUBPATH + SLASH + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
        .andExpect(status().isNoContent());

    mvc.perform(
            get(BASE_PATH_V1 + INVESTORS_SUBPATH + SLASH + id)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.idNumber", is("76245691H")))
        .andExpect(jsonPath("$.name", is("Manolo Rodriguez")))
        .andExpect(jsonPath("$.age", is(38)));
  }

  @Test
  @WithMockUser(roles = USER)
  public void givenNoInvestor_whenUpdateInvestor_thenStatus404() throws Exception {
    // given
    final Long id = 10L;

    final InvestorRequest request =
        InvestorRequest.builder()
            .idNumber("76245691H")
            .name("Manolo Rodriguez")
            .age(38)
            .country("SPAIN")
            .build();

    // when then
    mvc.perform(
            put(BASE_PATH_V1 + INVESTORS_SUBPATH + SLASH + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
        .andExpect(status().isNotFound());
  }

  private static String asJson(Object obj) throws JsonProcessingException {
    return mapper.writeValueAsString(obj);
  }
}
