package org.example.restful.configuration;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.example.restful.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class CustomControllerAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public final ResponseEntity<Error> handleNotFoundExceptions(
      final Exception ex, final WebRequest request) {
    log.error(
        "Response to {} with status {} and body {}",
        request,
        HttpStatus.NOT_FOUND,
        "Investor not found");

    final Error error = new Error("ERR404", "Investor not found");

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = {AccessDeniedException.class})
  public ResponseEntity<Error> handleAccessDeniedExceptions(
      HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
      throws IOException {

    log.error(
        "Response to {} with status {} and body {}",
        request,
        HttpStatus.UNAUTHORIZED,
        "Unauthorized");

    final Error error = new Error("ERR401", "Unauthorized user");

    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(Exception.class)
  public final ResponseEntity<Object> handleAllExceptions(
      final Exception ex, final WebRequest request) {
    log.error(
        "Response to {} with status {} and body {}",
        request,
        HttpStatus.INTERNAL_SERVER_ERROR,
        ex.getMessage());

    final Error error = new Error("ERR500", "Unknown error");

    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
