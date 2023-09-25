package org.example.restful.exception;

public class StockNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -8538212400867405969L;

  public StockNotFoundException() {
    super("Stock not found");
  }
}
