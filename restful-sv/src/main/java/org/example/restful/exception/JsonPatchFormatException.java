package org.example.restful.exception;

public class JsonPatchFormatException extends RuntimeException {

  private static final long serialVersionUID = 7407355382272705994L;

  public JsonPatchFormatException() {
    super("Invalid patch format");
  }
}
