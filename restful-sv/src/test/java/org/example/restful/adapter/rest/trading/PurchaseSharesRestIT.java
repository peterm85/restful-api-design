package org.example.restful.adapter.rest.trading;

import org.example.restful.port.rest.v1.api.model.OrderTypeRequest;
import org.example.restful.port.rest.v1.api.model.PurchaseBatchRequest;
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

import java.util.List;

import static org.example.restful.constant.Roles.ADMIN;
import static org.example.restful.constant.Roles.USER;
import static org.example.restful.constant.UrlConstants.BASE_PATH_V1;
import static org.example.restful.constant.UrlConstants.INVESTORS_SUBPATH;
import static org.example.restful.constant.UrlConstants.PURCHASE_ACTION;
import static org.example.restful.constant.UrlConstants.SLASH;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class PurchaseSharesRestIT {

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
    final Long id = 1L;

    final PurchaseRequest request =
        PurchaseRequest.builder()
            .isin(isin)
            .amount(150)
            .limitedPrize(Double.valueOf("3.45"))
            .orderType(OrderTypeRequest.DAILY)
            .build();

    // when then
    mvc.perform(
            post(BASE_PATH_V1 + INVESTORS_SUBPATH + SLASH + id + PURCHASE_ACTION)
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
    final Long id = 1L;

    final PurchaseRequest request = PurchaseRequest.builder().isin(isin).build();

    // when then
    mvc.perform(
            post(BASE_PATH_V1 + INVESTORS_SUBPATH + SLASH + id + PURCHASE_ACTION)
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
    final Long id = 1L;

    final PurchaseRequest request =
        PurchaseRequest.builder()
            .isin(isin)
            .amount(150)
            .limitedPrize(Double.valueOf("3.45"))
            .orderType(OrderTypeRequest.DAILY)
            .build();

    // when then
    mvc.perform(
            post(BASE_PATH_V1 + INVESTORS_SUBPATH + SLASH + id + PURCHASE_ACTION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @ParameterizedTest
  @CsvSource({"MX0105611000,1", "ES0105611000,10"})
  @WithMockUser(roles = USER)
  @SqlGroup({
    @Sql(value = "classpath:init/data-investor.sql", executionPhase = BEFORE_TEST_METHOD),
    @Sql(
        value = "classpath:init/data-stock.sql",
        config = @SqlConfig(encoding = "utf-8"),
        executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  })
  public void givenAnUnknowStockOrInvestor_whenPurchaseShares_thenStatus404(String isin, Long id)
      throws Exception {
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
            post(BASE_PATH_V1 + INVESTORS_SUBPATH + SLASH + id + PURCHASE_ACTION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = ADMIN)
  @SqlGroup({
    @Sql(value = "classpath:init/data-investor.sql", executionPhase = BEFORE_TEST_METHOD),
    @Sql(
        value = "classpath:init/data-stock.sql",
        config = @SqlConfig(encoding = "utf-8"),
        executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  })
  public void whenBatchPurchaseShares_thenStatus202() throws Exception {
    // given
    final String isin = "ES0105611000";

    final PurchaseBatchRequest request =
        PurchaseBatchRequest.builder()
            .investorId(1L)
            .isin(isin)
            .amount(150)
            .limitedPrize(Double.valueOf("3.45"))
            .orderType(OrderTypeRequest.DAILY)
            .build();
    final PurchaseBatchRequest request2 =
        PurchaseBatchRequest.builder()
            .investorId(2L)
            .isin(isin)
            .amount(50)
            .limitedPrize(Double.valueOf("3.45"))
            .orderType(OrderTypeRequest.DAILY)
            .build();

    // when then
    mvc.perform(
            patch(BASE_PATH_V1 + INVESTORS_SUBPATH + PURCHASE_ACTION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(List.of(request, request2))))
        .andExpect(status().isAccepted());
  }

  private static String asJson(Object obj) throws JsonProcessingException {
    return mapper.writeValueAsString(obj);
  }
}
