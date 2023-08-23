package org.example.restful;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
@SecurityScheme(
    name = "RestfulAPI",
    scheme = "basic",
    type = SecuritySchemeType.HTTP,
    in = SecuritySchemeIn.HEADER)
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
