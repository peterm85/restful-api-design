package org.example.restful.exception;

public class InvestorNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -8538212400867405969L;

  public InvestorNotFoundException() {
    super("Investor not found");
  }
}
