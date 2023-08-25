package org.example.restful.adapter.rest.v2.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.example.restful.adapter.rest.v2.controller.InvestorControllerV2Impl.PATH;
import static org.example.restful.adapter.rest.v2.controller.InvestorControllerV2Impl.SUBPATH;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InvestorControllerV2ImplIntegrationTest {

  @Autowired private MockMvc mvc;

  @Test
  @WithMockUser(roles = "ADMIN")
  @Sql(value = "classpath:init/test_data.sql", executionPhase = BEFORE_TEST_METHOD)
  public void givenPageAndSizewhenGetAllInvestors_thenStatus200() throws Exception {
    // when then
    mvc.perform(
            get(PATH + SUBPATH)
                .queryParam("page", "1")
                .queryParam("size", "3")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].idNumber", is("56245881H")))
        .andExpect(jsonPath("$[1].idNumber", is("71005791S")))
        .andExpect(jsonPath("$[2].idNumber", is("70335591K")));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void givenNoPageNoneSizewhenGetAllInvestors_thenStatus400() throws Exception {
    // when then
    mvc.perform(get(PATH + SUBPATH).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "USER")
  public void givenInvalidAuthRolewhenGetAllInvestors_thenStatus401() throws Exception {
    // when then
    mvc.perform(
            get(PATH + SUBPATH)
                .queryParam("page", "1")
                .queryParam("size", "3")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }
}
