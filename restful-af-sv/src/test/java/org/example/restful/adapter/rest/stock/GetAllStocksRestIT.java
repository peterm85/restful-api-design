package org.example.restful.adapter.rest.stock;

import org.example.restful.adapter.rest.AbstractRestIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.example.restful.constant.Roles.USER;
import static org.example.restful.constant.UrlConstants.BASE_PATH_V1;
import static org.example.restful.constant.UrlConstants.STOCKS_SUBPATH;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetAllStocksRestIT extends AbstractRestIT {

  @Test
  @WithMockUser(roles = USER)
  @SqlGroup({
    @Sql(
        value = "classpath:init/data-stock.sql",
        config = @SqlConfig(encoding = "utf-8"),
        executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  })
  public void whenGetAllStocks_thenStatus200() throws Exception {

    // when then
    mvc.perform(get(BASE_PATH_V1 + STOCKS_SUBPATH).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].corporationName", is("Singular People")))
        .andExpect(jsonPath("$[1].corporationName", is("BBVA")))
        .andExpect(jsonPath("$[2].corporationName", is("Telefónica")))
        .andExpect(header().string("Cache-Control", equalTo("max-age=70")))
        .andExpect(header().string("Last-Modified", notNullValue()));
  }
}
