package org.example.restful.configuration;

import org.example.restful.exception.InvestorException;
import org.example.restful.exception.InvestorNotFoundException;
import org.example.restful.exception.JsonPatchFormatException;
import org.example.restful.exception.StockException;
import org.example.restful.exception.StockNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class CustomControllerAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler({InvestorNotFoundException.class, StockNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public final Error handleNotFoundExceptions(final Exception ex, final WebRequest request) {
    log.error(
        "Response to {} with status {} and body {}",
        request,
        HttpStatus.NOT_FOUND,
        ex.getMessage());

    return new Error("ERR404", ex.getMessage());
  }

  @ExceptionHandler({InvestorException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public final Error handleInvestorExceptions(final Exception ex, final WebRequest request) {
    log.error(
        "Response to {} with status {} and body {}",
        request,
        HttpStatus.BAD_REQUEST,
        ex.getMessage());

    return new Error("ERR400", ex.getMessage());
  }

  @ExceptionHandler({StockException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public final Error handleStockExceptions(final Exception ex, final WebRequest request) {
    log.error(
        "Response to {} with status {} and body {}",
        request,
        HttpStatus.BAD_REQUEST,
        ex.getMessage());

    return new Error("ERR400", ex.getMessage());
  }

  @ExceptionHandler(value = {AccessDeniedException.class})
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  public Error handleAccessDeniedExceptions(
      HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
      throws IOException {

    log.error(
        "Response to {} with status {} and body {}",
        request,
        HttpStatus.UNAUTHORIZED,
        "Unauthorized");

    return new Error("ERR401", "Unauthorized user");
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    Error error = new Error("ERR400", "Invalid parameters ".concat(errors.toString()));
    return handleExceptionInternal(ex, error, headers, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(JsonPatchFormatException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  public final Object handleJsonPatchFormatExceptions(
      final JsonPatchFormatException ex, final WebRequest request) {
    log.error(
        "Response to {} with status {} and body {}", request, HttpStatus.CONFLICT, ex.getMessage());

    return new Error("ERR409", ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public final Object handleAllExceptions(final Exception ex, final WebRequest request) {
    log.error(
        "Response to {} with status {} and body {}",
        request,
        HttpStatus.INTERNAL_SERVER_ERROR,
        ex.getMessage());

    return new Error("ERR500", "Unknown error");
  }
}
