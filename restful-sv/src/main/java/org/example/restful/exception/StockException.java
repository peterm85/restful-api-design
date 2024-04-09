package org.example.restful.exception;

public class StockException extends RuntimeException {

  private static final long serialVersionUID = -8538212400867405969L;

  public StockException(final String message) {
    super(message);
  }
}
