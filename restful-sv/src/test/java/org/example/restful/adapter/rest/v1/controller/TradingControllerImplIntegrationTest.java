package org.example.restful.adapter.rest.v1.controller;

import org.example.restful.port.rest.v1.api.model.OrderTypeRequest;
import org.example.restful.port.rest.v1.api.model.PurchaseRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

import static org.example.restful.adapter.rest.v1.controller.TradingControllerImpl.PATH;
import static org.example.restful.adapter.rest.v1.controller.TradingControllerImpl.PURCHASE_OPERATION;
import static org.example.restful.adapter.rest.v1.controller.TradingControllerImpl.SLASH;
import static org.example.restful.adapter.rest.v1.controller.TradingControllerImpl.SUBPATH;
import static org.example.restful.constant.Roles.USER;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class TradingControllerImplIntegrationTest {

  @Autowired private MockMvc mvc;

  private static ObjectMapper mapper = new ObjectMapper();

  @Test
  @WithMockUser(roles = USER)
  @SqlGroup({
    @Sql(value = "classpath:init/data-investor.sql", executionPhase = BEFORE_TEST_METHOD),
    @Sql(
        value = "classpath:init/data-stock.sql",
        config = @SqlConfig(encoding = "utf-8"),
        executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  })
  public void whenPurchaseShares_thenStatus201() throws Exception {
    // given
    final String isin = "ES0105611000";
    final String idNumber = "76245691H";

    final PurchaseRequest request =
        PurchaseRequest.builder()
            .isin(isin)
            .amount(150)
            .limitedPrize(Double.valueOf("3.45"))
            .orderType(OrderTypeRequest.DAILY)
            .build();

    // when then
    mvc.perform(
            post(PATH + SUBPATH + SLASH + idNumber + SLASH + PURCHASE_OPERATION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.isin", is(isin)))
        .andExpect(jsonPath("$.amount", is(150)));
  }

  @Test
  @WithMockUser(roles = "USER")
  public void givenIncompletedBodyRequest_whenPurchaseShares_thenStatus400() throws Exception {
    // given
    final String isin = "ES0105611000";
    final String idNumber = "76245691H";

    final PurchaseRequest request = PurchaseRequest.builder().isin(isin).build();

    // when then
    mvc.perform(
            post(PATH + SUBPATH + SLASH + idNumber + SLASH + PURCHASE_OPERATION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  @WithMockUser(roles = "INVALID")
  public void givenInvalidAuthRole_whenPurchaseShares_thenStatus401() throws Exception {
    // given
    final String isin = "ES0105611000";
    final String idNumber = "76245691H";

    final PurchaseRequest request =
        PurchaseRequest.builder()
            .isin(isin)
            .amount(150)
            .limitedPrize(Double.valueOf("3.45"))
            .orderType(OrderTypeRequest.DAILY)
            .build();

    // when then
    mvc.perform(
            post(PATH + SUBPATH + SLASH + idNumber + SLASH + PURCHASE_OPERATION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @ParameterizedTest
  @CsvSource({"MX0105611000,76245691H", "ES0105611000,86245691H"})
  @WithMockUser(roles = USER)
  @SqlGroup({
    @Sql(value = "classpath:init/data-investor.sql", executionPhase = BEFORE_TEST_METHOD),
    @Sql(
        value = "classpath:init/data-stock.sql",
        config = @SqlConfig(encoding = "utf-8"),
        executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  })
  public void givenAnUnknowStockOrInvestor_whenPurchaseShares_thenStatus404(
      String isin, String idNumber) throws Exception {
    // given
    final PurchaseRequest request =
        PurchaseRequest.builder()
            .isin(isin)
            .amount(150)
            .limitedPrize(Double.valueOf("3.45"))
            .orderType(OrderTypeRequest.DAILY)
            .build();

    // when then
    mvc.perform(
            post(PATH + SUBPATH + SLASH + idNumber + SLASH + PURCHASE_OPERATION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
        .andExpect(status().isNotFound());
  }

  private static String asJson(Object obj) throws JsonProcessingException {
    return mapper.writeValueAsString(obj);
  }
}
