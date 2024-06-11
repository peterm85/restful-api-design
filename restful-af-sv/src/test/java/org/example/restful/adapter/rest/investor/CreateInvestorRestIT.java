package org.example.restful.adapter.rest.investor;

import org.example.restful.adapter.rest.AbstractRestIT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openapitools.model.InvestorRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

import lombok.SneakyThrows;

import static org.example.restful.constant.Roles.USER;
import static org.example.restful.constant.UrlConstants.BASE_PATH_V1;
import static org.example.restful.constant.UrlConstants.INVESTORS_SUBPATH;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasKey;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CreateInvestorRestIT extends AbstractRestIT {

  private static Stream<Arguments> createInvestorTestScenarios() {
    return Stream.of(
        Arguments.of(
            "When new investor is requested",
            InvestorRequest.builder()
                .idNumber("11222333X")
                .name("Olga Carmona")
                .age(23)
                .country("GRECIA")
                .build()));
  }

  @SneakyThrows
  @ParameterizedTest(name = "{index} {0}")
  @MethodSource("createInvestorTestScenarios")
  @DisplayName(
      "GIVEN a valid investor request "
          + "WHEN create investor is called "
          + "THEN a response with 201 is returned")
  @WithMockUser(roles = USER)
  @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  public void createInvestor(final String testName, final InvestorRequest request) {
    mvc.perform(
            post(BASE_PATH_V1 + INVESTORS_SUBPATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.idNumber", is(request.getIdNumber())))
        .andExpect(jsonPath("$.name", is(request.getName())))
        .andExpect(jsonPath("$.age", is(request.getAge())))
        .andExpect(jsonPath("$.country", is(request.getCountry())))
        .andExpect(jsonPath("$._links", hasKey("get")))
        .andExpect(jsonPath("$._links", hasKey("put")))
        .andExpect(jsonPath("$._links", hasKey("delete")));
  }

  private static Stream<Arguments> createInvestor_unhappyPathsTestScenarios() {
    return Stream.of(
        Arguments.of(
            "When investor already exists",
            InvestorRequest.builder()
                .idNumber("76245691H")
                .name("Manuel Rodriguez")
                .age(37)
                .country("SPAIN")
                .build(),
            "user1",
            USER,
            HttpStatus.BAD_REQUEST),
        Arguments.of(
            "When incompleted body is requested",
            InvestorRequest.builder().idNumber("11222333X").build(),
            "user1",
            USER,
            HttpStatus.BAD_REQUEST),
        Arguments.of(
            "When invalid role",
            InvestorRequest.builder()
                .idNumber("76245691H")
                .name("Manuel Rodriguez")
                .age(37)
                .country("SPAIN")
                .build(),
            "unknown",
            "UNKNOWN",
            HttpStatus.UNAUTHORIZED));
  }

  @SneakyThrows
  @ParameterizedTest(name = "{index} {0}")
  @MethodSource("createInvestor_unhappyPathsTestScenarios")
  @DisplayName(
      "GIVEN a invalid investor request "
          + "WHEN create investor is called "
          + "THEN a response with 40x is returned")
  @Sql(value = "classpath:init/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
  @Sql(value = "classpath:init/data-investor.sql", executionPhase = BEFORE_TEST_METHOD)
  public void createInvestor_unhappyPaths(
      final String testName,
      final InvestorRequest request,
      final String user,
      final String role,
      final HttpStatus expectedStatus) {
    mvc.perform(
            post(BASE_PATH_V1 + INVESTORS_SUBPATH)
                .with(user(user).roles(role))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request)))
        .andExpect(status().is(expectedStatus.value()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }
}
