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

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.example.restful.adapter.rest.v1.controller.InvestorControllerImpl.PATH;
import static org.example.restful.adapter.rest.v1.controller.InvestorControllerImpl.SLASH;
import static org.example.restful.adapter.rest.v1.controller.InvestorControllerImpl.SUBPATH;
import static org.example.restful.constant.Roles.USER;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class DeleteInvestorRestIT {

  @Autowired private MockMvc mvc;

  private static ObjectMapper mapper = new ObjectMapper();

  @Test
  @WithMockUser(roles = USER)
  @SqlGroup({
    @Sql(value = "classpath:init/data-investor.sql", executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  })
  public void whenDeleteInvestor_thenStatus204() throws Exception {
    // given
    final Long id = 1L;

    // when then
    mvc.perform(delete(PATH + SUBPATH + SLASH + id).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    mvc.perform(get(PATH + SUBPATH + SLASH + id).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
