package org.example.restful.adapter.rest.stock;

import org.example.restful.adapter.rest.AbstractRestIT;
import org.junit.jupiter.api.Test;
import org.openapitools.model.JsonPatchOperation;
import org.openapitools.model.JsonPatchRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;

import static org.example.restful.constant.Roles.ADMIN;
import static org.example.restful.constant.UrlConstants.BASE_PATH_V1;
import static org.example.restful.constant.UrlConstants.SLASH;
import static org.example.restful.constant.UrlConstants.STOCKS_SUBPATH;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UpdateStockRestIT extends AbstractRestIT {

  @Test
  @WithMockUser(roles = ADMIN)
  @SqlGroup({
    @Sql(
        value = "classpath:init/data-stock.sql",
        config = @SqlConfig(encoding = "utf-8"),
        executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  })
  public void whenUpdateStock_thenStatus200() throws Exception {
    // given
    final String isin = "ES0105611000";

    final JsonPatchOperation operation1 =
        JsonPatchOperation.builder()
            .op("replace")
            .path("/corporationName")
            .value("S|ngular People")
            .build();
    final JsonPatchOperation operation2 =
        JsonPatchOperation.builder().op("replace").path("/currency").value("USD").build();
    final JsonPatchRequest request =
        JsonPatchRequest.builder().operations(List.of(operation1, operation2)).build();

    // when then
    mvc.perform(
            patch(BASE_PATH_V1 + STOCKS_SUBPATH + SLASH + isin)
                .contentType("application/json-patch+json")
                .content(asJson(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.isin", is("ES0105611000")))
        .andExpect(jsonPath("$.corporationName", is("S|ngular People")))
        .andExpect(jsonPath("$.market", is("MAD")))
        .andExpect(jsonPath("$.currency", is("USD")))
        .andExpect(jsonPath("$._links", hasKey("get")))
        .andExpect(jsonPath("$._links", hasKey("patch")));
  }
}
