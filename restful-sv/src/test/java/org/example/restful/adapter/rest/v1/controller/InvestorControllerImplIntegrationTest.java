package org.example.restful.adapter.rest.v1.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.example.restful.adapter.rest.v1.controller.InvestorControllerImpl.PATH;
import static org.example.restful.adapter.rest.v1.controller.InvestorControllerImpl.SLASH;
import static org.example.restful.adapter.rest.v1.controller.InvestorControllerImpl.SUBPATH;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class InvestorControllerImplIntegrationTest {

  @Autowired private MockMvc mvc;

  @Test
  @WithMockUser(roles = "USER")
  public void givenIdNumber_whenGetInvestorByIdNumber_thenStatus200() throws Exception {
    // given
    final String idNumber = "76245691H";

    // when then
    mvc.perform(get(PATH + SUBPATH + SLASH + idNumber).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
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
}
