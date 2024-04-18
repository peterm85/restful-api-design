package org.example.restful.adapter.rest.investor;

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

import static org.example.restful.constant.Roles.ADMIN;
import static org.example.restful.constant.Roles.USER;
import static org.example.restful.constant.UrlConstants.BASE_PATH_V1;
import static org.example.restful.constant.UrlConstants.BASE_PATH_V2;
import static org.example.restful.constant.UrlConstants.INVESTORS_SUBPATH;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class GetAllInvestorsRestIT {

  @Autowired private MockMvc mvc;

  @Test
  @WithMockUser(roles = ADMIN)
  public void whenGetAllInvestors_thenStatus409() throws Exception {
    // given
    final String expectedLocationRedirection =
        BASE_PATH_V2 + INVESTORS_SUBPATH + "?offset=0&limit=30";

    // when then
    mvc.perform(get(BASE_PATH_V1 + INVESTORS_SUBPATH).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isPermanentRedirect())
        .andExpect(header().string("location", equalTo(expectedLocationRedirection)));
  }

  @Test
  @WithMockUser(roles = ADMIN)
  @SqlGroup({
    @Sql(value = "classpath:init/data-investor.sql", executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  })
  public void givenPageAndSizewhenGetAllInvestorsV2_thenStatus206() throws Exception {
    // when then
    mvc.perform(
            get(BASE_PATH_V2 + INVESTORS_SUBPATH)
                .queryParam("offset", "0")
                .queryParam("limit", "3")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isPartialContent())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data", hasSize(3)))
        .andExpect(jsonPath("$.data.[0].idNumber", is("76245691H")))
        .andExpect(jsonPath("$.data.[1].idNumber", is("03241601G")))
        .andExpect(jsonPath("$.data.[2].idNumber", is("21115691P")));
  }

  @Test
  @WithMockUser(roles = ADMIN)
  @SqlGroup({
    @Sql(value = "classpath:init/data-investor.sql", executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  })
  public void givenPageAndSizewhenGetAllInvestorsV2_thenStatus200() throws Exception {
    // when then
    mvc.perform(
            get(BASE_PATH_V2 + INVESTORS_SUBPATH)
                .queryParam("offset", "0")
                .queryParam("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data", hasSize(6)))
        .andExpect(jsonPath("$.data.[0].idNumber", is("76245691H")))
        .andExpect(jsonPath("$.data.[1].idNumber", is("03241601G")))
        .andExpect(jsonPath("$.data.[2].idNumber", is("21115691P")));
  }

  @Test
  @WithMockUser(roles = ADMIN)
  public void givenNoPageNoneSizewhenGetAllInvestorsV2_thenStatus200() throws Exception {
    // when then
    mvc.perform(get(BASE_PATH_V2 + INVESTORS_SUBPATH).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = USER)
  public void givenInvalidAuthRolewhenGetAllInvestorsV2_thenStatus401() throws Exception {
    // when then
    mvc.perform(
            get(BASE_PATH_V2 + INVESTORS_SUBPATH)
                .queryParam("offset", "0")
                .queryParam("limit", "3")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }
}
